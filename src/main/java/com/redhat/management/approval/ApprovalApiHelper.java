package com.redhat.management.approval;

import java.io.Serializable;

import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class ApprovalApiHelper implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private final static String DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final static String DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private final static String SKIP = "{\"operation\": \"skip\", \"processed_by\": \"system\"}";
    private final static String NOTIFY = "{\"operation\": \"notify\", \"processed_by\": \"system\"}";


    public static String formatDate(String pattern, String timeStr) throws Exception {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(getDate(timeStr));
    }

    public static Date getDate(String timeStr) throws Exception {
        Date date = getDate(timeStr, DATE_FORMAT_1);
        if (date == null) {
            date = getDate(timeStr, DATE_FORMAT_2);
        }

        if (date == null) {
            throw new Exception("Cannot parse: " + timeStr);
        }
        return date;
    }

    private static Date getDate(String timeStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date requestDate = null;

        try {
            requestDate = sdf.parse(timeStr); 
        }
        catch (ParseException e) {
            //do nothing, return null
        }
        return requestDate;
    }

    // Used in bpmn
    public static Stage findStageByGroup(Group group, List<Stage> stages) {
        for (Stage stage : stages) {
            if (stage.getGroupRef().equals(group.getUuid()))
                return stage;
        }
        return null; //TODO Exception handler
    }

    // Used in bpmn
    public static String getStageContent(String decision) {
        return isStageSkippable(decision) ? SKIP : NOTIFY;
    }

    // Used in bpmn
    public static String getStageUrl(Stage stage) {
        String apiUrl = System.getenv("APPROVAL_API_URL");
        return apiUrl + "/api/approval/v1.0/stages/"+ stage.getId() +"/actions";
    }

    public static boolean isStageSkippable(String action) {
        return (action.equals("denied") || action.equals("canceled"));
    }
}
