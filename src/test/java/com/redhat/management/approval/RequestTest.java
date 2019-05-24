package com.redhat.management.approval;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RequestTest {
    private LinkedHashMap<String, Object> rawRequest;

    @Before
    public void setUp() {
        rawRequest = TestResources.getRawRequest();
    }

    @Test
    public void testGetRHIdentity() {
        Request request = new Request(rawRequest);
        RHIdentity id = request.getRHIdentity();

        assertEquals(id.getUser().getUsername(), "jdoe");
        assertEquals(id.getUser().getFirst_name(), "John");
        assertEquals(id.getUser().getLast_name(), "Doe");
        assertEquals(id.getUser().getEmail(), "jdoe@acme.com");
    }

    @Test
    public void testCreateSysadminIdentity() {
        Request request = new Request(rawRequest);
        String sysadmin = request.createSysadminIdentity();

        assertNotNull(sysadmin);
        assertEquals(sysadmin, TestResources.ENCODED_SYSADMIN);
    }
}

