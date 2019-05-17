package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ApproveEmailBodyTest {
    private LinkedHashMap<String, Object> rawRequest;
    private ArrayList<LinkedHashMap<String, Object>> rawGroups;
    private ArrayList<LinkedHashMap<String, Object>> rawStages;

    @Before
    public void setUp() {
        rawRequest = TestResources.getRawRequest();
        rawGroups = TestResources.getRawGroups();
        rawStages = TestResources.getRawStages();
    }

    @Test
    public void testGetEmailTemplate() {
      Request request = InputParser.parseRequest(rawRequest);
      ArrayList<Stage> stages = InputParser.parseStages(rawStages);
      ArrayList<Group> groups = InputParser.parseGroups(rawGroups);
      ArrayList<Approver> approvers = groups.get(0).getApprovers();
      ApproveEmailBody body = new ApproveEmailBody(request, 
          approvers.get(0), groups.get(0), stages);

      String template = body.getEmailTemplate();

      assertNotNull(template);
      assertTrue(template.startsWith("<html>"));
    }

    @Test
    public void testGetRequestParameters() {
      Request request = InputParser.parseRequest(rawRequest);
      ArrayList<Stage> stages = InputParser.parseStages(rawStages);
      ArrayList<Group> groups = InputParser.parseGroups(rawGroups);
      ArrayList<Approver> approvers = groups.get(0).getApprovers();
      ApproveEmailBody body = new ApproveEmailBody(request, 
          approvers.get(0), groups.get(0), stages);

      HashMap<String, String> params = body.getRequestParameters();

      assertNotNull(params);
      System.out.println("params: " + params);
    }

    @Test
    public void testCustomizeKey() {
      assertEquals(ApproveEmailBody.customizeKey("order_id"), "Order ID");
      assertEquals(ApproveEmailBody.customizeKey("order_Id"), "Order ID");
      assertEquals(ApproveEmailBody.customizeKey("order_iD"), "Order ID");
    }
}
