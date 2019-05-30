package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EmailBodyTest {
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
      List<Stage> stages = InputParser.parseStages(rawStages);
      List<Group> groups = InputParser.parseGroups(rawGroups);
      List<Approver> approvers = groups.get(0).getApprovers();
      EmailBody body = new EmailBody(request, 
          approvers.get(0), groups.get(0), stages);

      String template = body.getEmailTemplate();

      assertNotNull(template);
      assertTrue(template.startsWith("<html>"));
    }

    @Test
    public void testGetRequestParameters() {
      Request request = InputParser.parseRequest(rawRequest);
      List<Stage> stages = InputParser.parseStages(rawStages);
      List<Group> groups = InputParser.parseGroups(rawGroups);
      List<Approver> approvers = groups.get(0).getApprovers();
      EmailBody body = new EmailBody(request, 
          approvers.get(0), groups.get(0), stages);

      Map<String, String> params = body.getRequestParameters();

      assertNotNull(params);
      System.out.println("params: " + params);
    }

    @Test
    public void testCustomizeKey() {
      Request request = InputParser.parseRequest(rawRequest);
      List<Stage> stages = InputParser.parseStages(rawStages);
      List<Group> groups = InputParser.parseGroups(rawGroups);
      List<Approver> approvers = groups.get(0).getApprovers();
      EmailBody body = new EmailBody(request, 
          approvers.get(0), groups.get(0), stages);

      assertEquals(body.customizeKey("order_id"), "Order ID");
      assertEquals(body.customizeKey("order_Id"), "Order ID");
      assertEquals(body.customizeKey("order_iD"), "Order ID");
    }
}
