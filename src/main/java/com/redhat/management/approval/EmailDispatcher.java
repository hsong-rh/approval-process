package com.redhat.management.approval;

import java.util.ArrayList;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class EmailDispatcher implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	@org.kie.api.definition.type.Label("Url")
	private java.lang.String url;
	@org.kie.api.definition.type.Label("Headers")
	private java.lang.String headers;
	@org.kie.api.definition.type.Label("Body")
	private java.lang.String body;

	public EmailDispatcher() {
	}

	public void setHeaders(java.lang.String headers) {
		this.headers = headers;
	}

	public void setUrl(java.lang.String url) {
		this.url = url;
	}

	public void setBody(java.lang.String body) {
		this.body = body;
	}

	public EmailDispatcher(java.lang.String headers, java.lang.String url,
			java.lang.String body) {
		this.headers = headers;
		this.url = url;
		this.body = body;
	}

	public String getUrl() {
		return "https://access.redhat.com/r/insights-services/v1/sendEmails";
	}

	public String getHeaders() {
		/*
		 * x-rh-clientid: <id> x-rh-apitoken: <token> x-rh-insights-env: <env>
		 * 
		 * key1=val1;key2=v2;key3=v3
		 */
		return "x-rh-clientid=approval-api;x-rh-apitoken=mcNoq2cpF+JGm+bO7wjNj6PyyR5HQJh/eZYJal9Ai7WDo3fQBXi9l8L+9+WF7RnqoYyhhvEwFJQ2dyXGRT9RQeNAQ46Bj/ic6bNmijO+KJ1vVtu;x-rh-insights-env=qa";
	}

	public String getContent(ArrayList<com.redhat.management.approval.Approver> approvers,
	        com.redhat.management.approval.Group group, 
	        ArrayList<com.redhat.management.approval.Stage> stages,
			com.redhat.management.approval.Request request) {
		/*
		 { "emails": [ { "subject": "Here's an Email!", "body":
		 "<body><p>This is the email.</p></body>", "bodyType": "html",
		 "recipients": [ "\"Bill\" bilwei@redhat.com" ] } ] }
		 */
        ArrayList<ApproveEmail> emails = new ArrayList<ApproveEmail>();
		for (Approver approver : approvers) {
            String name = approver.getFirstName() + " " + approver.getLastName();
            Recipient recipient = new Recipient(name, approver.getEmailAddress());
            ArrayList<String> recipients = new ArrayList<String>();
            recipients.add(recipient.toString());
            ApproveEmail email = new ApproveEmail(recipients);
            email.setBody(request, approver, group, stages);
            emails.add(email);
		}
        
        ApproveEmailList list = new ApproveEmailList(emails);
        
        ObjectMapper mapper = new ObjectMapper();
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
		
		return jsonStr;

	}
	
	public static com.redhat.management.approval.Stage getCurrentStage(com.redhat.management.approval.Group group, 
	    java.util.ArrayList<com.redhat.management.approval.Stage> stages) {
	    for (Stage stage : stages) {
	        if (stage.getGroupId().equals(group.getUuid()))
	            return stage;
	    }
	    return null; //TODO Exception handler
	}
	
	public static String getStageContent(String signalContent) {
	   return signalContent.equals("skip") ? "{ :operation => 'skip', :processed_by => 'sysadmin' }" : "{ :operation => 'notify', :processed_by => 'sysadmin' }";
    }

    public static String getStageUrl(Stage stage) {
        // TODO: get hostname from ENV
        String hostname = "http://localhost:3000/api/v0.1";
        return hostname + "/stages/"+ stage.getId()+"/actions";
    }
}
