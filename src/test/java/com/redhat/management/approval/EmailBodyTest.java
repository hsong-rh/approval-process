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
    private RequestPacket rawRequestPacket;

    @Before
    public void setUp() {
        rawRequest = TestResources.getRawRequest();
        rawGroups = TestResources.getRawGroups();
        rawRequestPacket = TestResources.getRawRequestPacket();
    }

    @Test
    public void testGetEmailTemplate() {
      Request request = InputParser.parseRequest(rawRequest, rawRequestPacket);
      List<Group> groups = InputParser.parseGroups(rawGroups);
      List<Approver> approvers = groups.get(0).getApprovers();
      EmailBody body = new EmailBody(approvers.get(0), groups.get(0), request);

      String template = body.getEmailTemplate();

      assertNotNull(template);
      assertTrue(template.startsWith("<!DOCTYPE html"));
    }
}
