package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EmailDispatcherTest {
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
    public void testGetContent() {
      Request request = InputParser.parseRequest(rawRequest, rawRequestPacket);
      List<Group> groups = InputParser.parseGroups(rawGroups);
      List<Approver> approvers = groups.get(0).getApprovers();
      EmailDispatcher dispatcher = new EmailDispatcher("headers", "url", "body");
      dispatcher.setBody(approvers, groups.get(0), request);

      String body = dispatcher.getBody();

      assertNotNull(body);
    }
}
