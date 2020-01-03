package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InputParserTest {
    private LinkedHashMap<String, Object> rawRequest;
    private List<LinkedHashMap<String, Object>> rawGroups;
    private RequestPacket rawRequestPacket;

    @Before
    public void setUp() {
        rawRequest = TestResources.getRawRequest();
        rawGroups = TestResources.getRawGroups();
        rawRequestPacket = TestResources.getRawRequestPacket();
    }

    @Test
    public void testParseRequest() {
        Request request = InputParser.parseRequest(rawRequest, rawRequestPacket);

        assertEquals(request.getRequester(), TestResources.REQUESTER);
        assertEquals(request.getName(), TestResources.REQUEST_NAME);
    }

    @Test
    public void testParseGroups() {
        List<Group> groups = InputParser.parseGroups(rawGroups);

        assertEquals(groups.size(), 1);
        assertEquals(groups.get(0).getName(), TestResources.GROUP_NAME);
        assertEquals(groups.get(0).getUuid(), TestResources.GROUP_REF);
        assertEquals(groups.get(0).getApprovers().size(), 2);
        assertEquals(groups.get(0).getApprovers().get(0).getUserName(), TestResources.FIRST_USERNAME);
        assertEquals(groups.get(0).getApprovers().get(0).getEmailAddress(), TestResources.FIRST_EMAIL);
        assertEquals(groups.get(0).getApprovers().get(0).getFirstName(), TestResources.FIRST_FNAME);
        assertEquals(groups.get(0).getApprovers().get(0).getLastName(), TestResources.FIRST_LNAME);
    }
}
