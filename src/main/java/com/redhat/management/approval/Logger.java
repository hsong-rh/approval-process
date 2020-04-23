package com.redhat.management.approval;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.InvalidSequenceTokenException;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.amazonaws.services.logs.model.RejectedLogEventsInfo;
import com.amazonaws.services.logs.model.ResourceAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Logger implements Runnable{
    static private final String PID = getProcessId();
    static private final Logger LOGGER = new Logger();
    static private final long FORCE_FLUSH_INTERVAL = 1000L;

    private final BlockingQueue<InputLogEvent> eventQueue = new LinkedBlockingQueue<InputLogEvent>();;
    private final Thread writerThread;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> futureHandle;
    private final Runnable futureTask;
    private final AWSLogsClient client;
    private String nextToken;
    private long lastFlushTime;
    private final ThreadLocal<String> httpRequestId = new ThreadLocal<String>();
    private final String logGroupName = System.getenv("CLOUD_WATCH_LOG_GROUP");
    private final String logStreamName = System.getenv("HOSTNAME");
    private final String accessKeyId = System.getenv("CW_AWS_ACCESS_KEY_ID");
    private final String secretKey = System.getenv("CW_AWS_SECRET_ACCESS_KEY");
    private final String awsRegion = System.getenv("CW_AWS_REGION") ;
    private final String hostName = System.getenv("HOSTNAME");

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Logger getLogger(String httpRequestId) {
        LOGGER.setHttpRequestId(httpRequestId);
        return LOGGER;
    }

    private Logger() {
        client = initCloudWatchClient();

        futureTask = new Runnable() {
            public void run() {
                Logger.getLogger().sendLogs(true);
            }
        };

        writerThread = new Thread(this, "CloudWatch Writer");
        writerThread.setDaemon(true);
        writerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Logger shutting down");
                try {
                    scheduler.shutdown();
                }catch(Exception e) {
                    System.out.println("Failed to terminate scheduler");
                }
                Logger.getLogger().sendLogs(true);
                if (client != null) {
                    client.shutdown();
                }
            }
        });
    }

    public void interrupt() {
        writerThread.interrupt();
    }

    public void debug(String msg) {
        log("debug", msg);
    }

    public void debug(String msg, Throwable t) {
        log("debug", throwableToString(msg, t));
    }

    public void info(String msg) {
        log("info", msg);
    }

    public void info(String msg, Throwable t) {
        log("info", throwableToString(msg, t));
    }

    public void warn(String msg) {
        log("warning", msg);
    }

    public void warn(String msg, Throwable t) {
        log("warning", throwableToString(msg, t));
    }

    public void error(String msg) {
        log("err", msg);
    }

    public void error(String msg, Throwable t) {
        log("err", throwableToString(msg, t));
    }

    private void log(String level, String msg) {
        eventQueue.add(createEvent(level, msg));
        synchronized (eventQueue) {
            eventQueue.notify();
        }
    }

    private InputLogEvent createEvent(String level, String msg) {
        Instant now = Instant.now();
        TreeMap<String, Object> hMsg = new TreeMap<String, Object>();
        hMsg.put("@timestamp", now.toString());
        hMsg.put("hostname", hostName);
        hMsg.put("pid", Logger.PID);
        hMsg.put("tid", String.valueOf(Thread.currentThread().getId()));
        hMsg.put("level", level);
        hMsg.put("message", msg);
        if (httpRequestId.get() != null) {
            hMsg.put("request_id", httpRequestId.get());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(hMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            json = msg;
        }

        InputLogEvent event = new InputLogEvent();
        event.setMessage(json);
        event.setTimestamp(now.toEpochMilli());

        return event;
    }

    private static String getProcessId() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        return jvmName.split("@")[0];
    }

    public void run() {
        boolean nonstop = true;
        while(nonstop) {
            synchronized (eventQueue) {
                try {
                    eventQueue.wait();
                } catch(InterruptedException e) {
                  // should not matter; wait again
                }
            }

            // reschedule by canceling the old one and creating a new one
            if (futureHandle != null && !futureHandle.isDone()) {
                futureHandle.cancel(false);
            }
            futureHandle = this.scheduler.schedule(futureTask, FORCE_FLUSH_INTERVAL, TimeUnit.MILLISECONDS);

            sendLogs(false);
        }
    }

    public synchronized void sendLogs(boolean force) {
        long now = System.currentTimeMillis();

        if(eventQueue.isEmpty()) {
            lastFlushTime = now;
            return;
        }

        if (!force && now - lastFlushTime < FORCE_FLUSH_INTERVAL) {
            return;
        }

        List<InputLogEvent> logs = new ArrayList<InputLogEvent>();
        eventQueue.drainTo(logs);

        for(InputLogEvent log : logs) {
            System.out.println(log.getMessage());
        }

        if (client != null) {
            logToCloud(logs);
        }
        lastFlushTime = now;
    }

    public void setHttpRequestId(String id) {
        httpRequestId.set(id);
    }

    private AWSLogsClient initCloudWatchClient() {
        if (this.accessKeyId == null || this.secretKey == null || this.logGroupName == null || this.logStreamName == null) {
            System.out.println("CloudWatch logger is disabled because configuration is incomplete.");
            return null;
        }
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);

        AWSLogsClient client = (AWSLogsClient) AWSLogsClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(awsRegion).build();

        boolean createLogStream = true;

        DescribeLogStreamsRequest request = new DescribeLogStreamsRequest().withLogGroupName(logGroupName).withLogStreamNamePrefix(logStreamName);
        try {
            DescribeLogStreamsResult result = client.describeLogStreams(request);
            for (LogStream stream : result.getLogStreams()) {
                if (stream.getLogStreamName().equals(logStreamName)) {
                    nextToken = stream.getUploadSequenceToken();
                    createLogStream = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            client = null;
            createLogStream = false;
        }

        if (createLogStream) {
            try {
                client.createLogStream(new CreateLogStreamRequest(logGroupName, logStreamName));
            } catch (ResourceAlreadyExistsException e) {
              //ignore, continue
            } catch (Exception e) {
                e.printStackTrace();
                client = null; //turn off CloudWatch logger
            }
        }
        return client;
    }

    private void logToCloud(List<InputLogEvent> logs) {
        PutLogEventsRequest logEventsRequest = new PutLogEventsRequest(logGroupName, logStreamName, logs);
        logEventsRequest.setSequenceToken(nextToken);
        try {
            PutLogEventsResult writeResult = client.putLogEvents(logEventsRequest);
            RejectedLogEventsInfo rejectInfo = writeResult.getRejectedLogEventsInfo();
            if (rejectInfo != null) {
                if(rejectInfo.getExpiredLogEventEndIndex() != null) {
                    System.out.println("CloudWatch ERROR: Some logs have expired.");
                }
                if(rejectInfo.getTooNewLogEventStartIndex() != null) {
                    System.out.println("CloudWatch ERROR: Some logs are too new.");
                }
                if(rejectInfo.getTooOldLogEventEndIndex() != null) {
                    System.out.println("CloudWatch ERROR: Some logs are too old.");
                }
            }

            nextToken = writeResult.getNextSequenceToken();
        } catch (InvalidSequenceTokenException e) {
            nextToken = e.getExpectedSequenceToken();
            logToCloud(logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String throwableToString(String msg, Throwable t) {
        StringWriter buffer = new StringWriter();
        buffer.write(msg);
        buffer.write("\n");
        t.printStackTrace(new PrintWriter(buffer));

        return buffer.toString();
    }
}
