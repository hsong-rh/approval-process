package com.redhat.management.approval;

import java.util.Base64;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class EmailBody implements Serializable {

    static final long serialVersionUID = 1L;
    private static String templateFile = "EmailTemplate.html";

    private Request request;
    private Approver approver;
    private Group group;
    private List<Stage> stages;

    public String getEmailTemplate() {
        URL url = EmailBody.class.getResource(templateFile);

        String content = "";
        try {
            content = getUrlContent(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public Map<String, String> getRequestParameters() {
        Stage currentStage = ApprovalApiHelper.findStageByGroup(group, stages);
        Map<String, Object> request_content = request.getContent();

        Map<String, String> values = new HashMap<String, String>();
        values.put("approver_name", approver.getFirstName() + " " + approver.getLastName());
        values.put("requester_name", (String) request.getIdentityFullName());
        values.put("orderer_email", request.getIdentityEmail());
        values.put("contents", getRequestContentLines(request_content));

        String webUrl = System.getenv("APPROVAL_WEB_URL");
        try {
            byte[] bytes = approver.getUserName().getBytes("UTF-8");
            String encoded_user = Base64.getEncoder().encodeToString(bytes);
            String approveLink = webUrl + "/api/approval/v1.0/stageaction/" + currentStage.getRandomAccessKey() + "?approver=" + encoded_user;
            values.put("approve_link", approveLink);

            String orderLink = webUrl + "/hybrid/catalog/approval/requests/detail/" + request.getId();
            values.put("order_link", orderLink);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace(); 
        }

        try {
            String date = ApprovalApiHelper.formatDate("dd MMM yyyy", request.getCreatedTime());
            String time = ApprovalApiHelper.formatDate("HH:mm:ss", request.getCreatedTime());
            values.put("order_date", date);
            values.put("order_time", time);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HashMap<String, String> params = (HashMap<String, String>) request_content.get("params");

        System.out.println("request content params: " + params);
        values.put("params", getParamsTable(params));
        values.put("approval_id", request.getId());

        return values;
    }

    public String customizeKey(String key) {
        return StringUtils.capitalize(key.replace("_", " ").replaceAll("(?i)id", "ID"));
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Approver getApprover() {
        return this.approver;
    }

    public void setApprover(Approver approver) {
        this.approver = approver;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Stage> getStages() {
        return this.stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public EmailBody(Request request, Approver approver, Group group, List<Stage> stages) {
        this.request = request;
        this.approver = approver;
        this.group = group;
        this.stages = stages;
    }

    public String toString() {
        String template = getEmailTemplate();
        Map<String, String> values = getRequestParameters();

        StrSubstitutor sub = new StrSubstitutor(values);
        return sub.replace(template);
    }

    private static String getUrlContent(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(
                              new InputStreamReader(
                                connection.getInputStream()));

        StringBuilder content = new StringBuilder();
        String inputLine;

        try {
            while ((inputLine = in.readLine()) != null) 
                content.append(inputLine);
        }
        finally {
            in.close();
        }

        return content.toString();
    }

    private String getRequestContentLines(Map<String, Object> contents) {
        StringBuilder lines = new StringBuilder();
        for (HashMap.Entry<String, Object> entry : contents.entrySet()) {
            if (entry.getKey().equals("params"))
                continue;
  
            String line = "<strong>" + customizeKey(entry.getKey()) + ":</strong>" + entry.getValue().toString() + "<br>";
            lines.append(line);
        }
        System.out.println("Request content: "+ lines);
        return lines.toString();
    }

    private String getParamsTable(HashMap<String, String> params) {
        StringBuilder paramsTable = new StringBuilder(
                "<table><tbody><tr><td><strong>Key</strong></td><td><strong>Value<strong></td></tr>\n");
        
        for (HashMap.Entry<String, String> entry: params.entrySet()) {
            String param = "<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>\n";
            paramsTable.append(param);
        };
        paramsTable.append("</tbody></table>");
        return paramsTable.toString();
    }

}
