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
		url = System.getenv("BACKOFFICE_URL");
		return url;
	}

	public String getHeaders() {
		/*
		 * x-rh-clientid: <id> x-rh-apitoken: <token> x-rh-insights-env: <env>
		 * 
		 * key1=val1;key2=v2;key3=v3
		 */
		String clientId = System.getenv("BACKOFFICE_CLIENT_ID");
		String token = System.getenv("BACKOFFICE_TOKEN");
		String clientEnv = System.getenv("BACKOFFICE_CLIENT_ENV");
		
		StringBuilder headers = new StringBuilder();
		headers.append("x-rh-clientid=");
		headers.append(clientId);
		headers.append(";x-rh-apitoken=");
		headers.append(token);
		headers.append(";x-rh-insights-env=");
		headers.append(clientEnv);
		
		return headers.toString();
	}

	public String getContent(ArrayList<com.redhat.management.approval.Approver> approvers,
	        com.redhat.management.approval.Group group, 
	        ArrayList<com.redhat.management.approval.Stage> stages,
			com.redhat.management.approval.Request request) {

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
	        if (stage.getGroupRef().equals(group.getUuid()))
	            return stage;
	    }
	    return null; //TODO Exception handler
	}
	
	public static String getStageContent(String decision) {
	    if (decision == null )
	        decision = "undecided";
	        
	   String skip = "{\"operation\": \"skip\", \"processed_by\": \"sysadmin\"}";
	   String notify = "{\"operation\": \"notify\", \"processed_by\": \"sysadmin\"}";
	        
	    return decision.equals("denied") ? skip : notify;
    }

    public static String getStageUrl(Stage stage) {
        String apiUrl = System.getenv("APPROVAL_API_URL");
        return apiUrl + "/"+ stage.getId()+"/actions";
    }
}
