package com.redhat.management.approval;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class EmailDispatcher implements Serializable {

    static final long serialVersionUID = 1L;

    private String url;
    private String headers;
    private String body;

    public EmailDispatcher(String headers, String url, String body) {
        this.headers = headers;
        this.url = url;
        this.body = body;
    }
    
    public EmailDispatcher(List<Approver> approvers, Group group, Request request) {
        setHeaders(defaultHeaders());
        setUrl(defaultUrl());
        setBody(approvers, group, request);
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String defaultUrl() {
        return System.getenv("BACKOFFICE_URL");
    }

    public String defaultHeaders() {
        /*
         * x-rh-clientid: <id> x-rh-apitoken: <token> x-rh-insights-env: <env>
         * 
         * key1=val1;key2=v2;key3=v3
         */
        String clientId = System.getenv("BACKOFFICE_CLIENT_ID");
        String token = System.getenv("BACKOFFICE_TOKEN").replace("=", "");
        String clientEnv = System.getenv("BACKOFFICE_CLIENT_ENV");
        
        StringBuilder headersBuilder = new StringBuilder();
        headersBuilder.append("x-rh-clientid=");
        headersBuilder.append(clientId);
        headersBuilder.append(";x-rh-apitoken=");
        headersBuilder.append(token);
        if (clientEnv != null) {
            headersBuilder.append(";x-rh-insights-env=");
            headersBuilder.append(clientEnv);
        }
        
        return headersBuilder.toString();
    }

    // Used in bpmn
    public void setBody(List<Approver> approvers, Group group, Request request) {
        String requestId = request.getParentId();
        String requestName = request.getName();
        
        ArrayList<Email> emails = new ArrayList<Email>();
        for (Approver approver : approvers) {
            String name = approver.getFirstName() + " " + approver.getLastName();
            Recipient recipient = new Recipient(name, approver.getEmailAddress());
            ArrayList<String> recipients = new ArrayList<String>();
            recipients.add(recipient.toString());
            Email email = new Email(recipients);
            email.setSubject(requestId, requestName);
            email.setBody(approver, group, request);
            emails.add(email);
        }

        EmailList list = new EmailList(emails);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStr = "";

        try {
            jsonStr = mapper.writeValueAsString(list);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.body = jsonStr;
    }

    public String getHeaders() {
        return this.headers;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getBody() {
        return this.body;
    }
}
