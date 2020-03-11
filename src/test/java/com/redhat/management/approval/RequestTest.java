package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RequestTest {
    private LinkedHashMap<String, Object> rawRequest;
    private RequestPacket rawRequestPacket;

    @Before
    public void setUp() {
        rawRequest = TestResources.getRawRequest();
        rawRequestPacket = TestResources.getRawRequestPacket();
    }

    @Test
    public void testGetRHIdentity() {
        Request request = new Request(rawRequest, rawRequestPacket);
        RHIdentity id = request.getRHIdentity();

        assertEquals(id.getUser().getUsername(), "jdoe");
        assertEquals(id.getUser().getFirst_name(), "John");
        assertEquals(id.getUser().getLast_name(), "Doe");
        assertEquals(id.getUser().getEmail(), "jdoe@acme.com");
    }

    @Test
    public void testGetPostActionHeaders() {
        Request request = new Request(rawRequest, rawRequestPacket);
        String headers = request.getPostActionHeaders();

        assertNotNull(headers);
        assertEquals(headers, "x-rh-identity="+TestResources.ENCODED_SYSADMIN);
    }
}

