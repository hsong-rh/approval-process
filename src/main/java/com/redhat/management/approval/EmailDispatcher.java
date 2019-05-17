package com.redhat.management.approval;

import java.util.ArrayList;
import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class EmailDispatcher implements Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Label("Url")
    private String url;
    @org.kie.api.definition.type.Label("Headers")
    private String headers;
    @org.kie.api.definition.type.Label("Body")
    private String body;

    public EmailDispatcher() {
    }

    public EmailDispatcher(String headers, String url,
            String body) {
        this.headers = headers;
        this.url = url;
        this.body = body;
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
        String token = System.getenv("BACKOFFICE_TOKEN").replace("=", "");
        String clientEnv = System.getenv("BACKOFFICE_CLIENT_ENV");
        
        StringBuilder headers = new StringBuilder();
        headers.append("x-rh-clientid=");
        headers.append(clientId);
        headers.append(";x-rh-apitoken=");
        headers.append(token);
        if (clientEnv != null) {
            headers.append(";x-rh-insights-env=");
            headers.append(clientEnv);
        }
        
        return headers.toString();
    }

    // Used in bpmn
    public String getContent(ArrayList<Approver> approvers,
          Group group, 
          ArrayList<Stage> stages,
          Request request) {
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
        
        return jsonStr;
    }

    // Used in bpmn
    public static Stage getCurrentStage(Group group,
        ArrayList<Stage> stages) {
        for (Stage stage : stages) {
            if (stage.getGroupRef().equals(group.getUuid()))
                return stage;
        }
        return null; //TODO Exception handler
    }

    // Used in bpmn
    public static String getStageContent(String decision) {
        if (decision == null )
            decision = "undecided";

        String skip = "{\"operation\": \"skip\", \"processed_by\": \"system\"}";
        String notify = "{\"operation\": \"notify\", \"processed_by\": \"system\"}";

        return decision.equals("denied") ? skip : notify;
    }

    // Used in bpmn
    public static String getStageUrl(Stage stage) {
        String apiUrl = System.getenv("APPROVAL_API_URL");
        return apiUrl + "/api/approval/v1.0/stages/"+ stage.getId()+"/actions";
    }
}
