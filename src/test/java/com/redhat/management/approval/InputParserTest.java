package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InputParserTest {
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
    public void testParseRequest() {
        Request request = InputParser.parseRequest(rawRequest);

        assertEquals(request.getRequester(), TestResources.REQUESTER);
        assertEquals(request.getDescription(), TestResources.DESCRIPTION);
        assertEquals(request.getName(), TestResources.REQUEST_NAME);
    }

    @Test
    public void testParseGroups() {
        ArrayList<Group> groups = InputParser.parseGroups(rawGroups);

        assertEquals(groups.size(), 1);
        assertEquals(groups.get(0).getName(), TestResources.GROUP_NAME);
        assertEquals(groups.get(0).getDescription(), TestResources.DESCRIPTION);
        assertEquals(groups.get(0).getUuid(), TestResources.GROUP_REF);
        assertEquals(groups.get(0).getApprovers().size(), 2);
        assertEquals(groups.get(0).getApprovers().get(0).getUserName(), TestResources.FIRST_USERNAME);
        assertEquals(groups.get(0).getApprovers().get(0).getEmailAddress(), TestResources.FIRST_EMAIL);
        assertEquals(groups.get(0).getApprovers().get(0).getFirstName(), TestResources.FIRST_FNAME);
        assertEquals(groups.get(0).getApprovers().get(0).getLastName(), TestResources.FIRST_LNAME);
    }

    @Test
    public void testParseStages() {
        ArrayList<Stage> stages = InputParser.parseStages(rawStages);

        assertEquals(stages.size(), 1);
        assertEquals(stages.get(0).getId(), TestResources.ID);
        assertEquals(stages.get(0).getRandomAccessKey(), TestResources.RANDOM_ACCESS_KEY);
        assertEquals(stages.get(0).getGroupRef(), TestResources.GROUP_REF);
    }
}
