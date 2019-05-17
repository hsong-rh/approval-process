package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EmailDispatcherTest {
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
    public void testGetContent() {
      EmailDispatcher dispatcher = new EmailDispatcher();
      Request request = InputParser.parseRequest(rawRequest);
      ArrayList<Stage> stages = InputParser.parseStages(rawStages);
      ArrayList<Group> groups = InputParser.parseGroups(rawGroups);
      ArrayList<Approver> approvers = groups.get(0).getApprovers();

      String content = dispatcher.getContent(approvers, groups.get(0), stages, request);

      assertNotNull(content);
    }

    @Test
    public void testGetCurrentStage() {
      ArrayList<Stage> stages = InputParser.parseStages(rawStages);
      ArrayList<Group> groups = InputParser.parseGroups(rawGroups);

      Stage stage = EmailDispatcher.getCurrentStage(groups.get(0), stages);

      assertNotNull(stage);
      assertEquals(stage.getId(), TestResources.ID);
    }
}
