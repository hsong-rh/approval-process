package com.redhat.management.approval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestResources {
    public static LinkedHashMap<String, Object> getRawRequest() {
        return prepareRawRequest();
    }

    public static ArrayList<LinkedHashMap<String, Object>> getRawGroups() {
        return prepareRawGroups();
    }

    public static RequestPacket getRawRequestPacket() {
        return prepareRawRequestPacket();
    }

    private static LinkedHashMap<String, Object> prepareRawRequest() {
        LinkedHashMap<String, Object> rawRequest = new LinkedHashMap();
        rawRequest.put("name", REQUEST_NAME);
        rawRequest.put("requester_name", REQUESTER);
        rawRequest.put("id", ID);
        rawRequest.put("parent_id", PARENT_ID);
        rawRequest.put("random_access_key", RANDOM_ACCESS_KEY);
        rawRequest.put("group_ref", GROUP_REF);
        rawRequest.put("group_name", GROUP_NAME);
        rawRequest.put("tenant_id", TENANT_ID);
        rawRequest.put("created_at", CREATED_AT);

        return rawRequest;
    }

    private static ArrayList<LinkedHashMap<String, Object>> prepareRawGroups() {
        ArrayList<LinkedHashMap<String, Object>> groups = new ArrayList();
        ArrayList<LinkedHashMap<String, Object>> users = new ArrayList();

        LinkedHashMap<String, Object> user1 = new LinkedHashMap();
        user1.put("username", FIRST_USERNAME);
        user1.put("email", FIRST_EMAIL);
        user1.put("first_name", FIRST_FNAME);
        user1.put("last_name", FIRST_LNAME);
        
        LinkedHashMap<String, Object> user2 = new LinkedHashMap();
        user2.put("username", "username_2");
        user2.put("email", "user_2_email");
        user2.put("first_name", "user_2_first_name");
        user2.put("last_name", "user_2_last_name");

        users.add(user1);
        users.add(user2);

        LinkedHashMap<String, Object> group = new LinkedHashMap();
        group.put("name", GROUP_NAME);
        group.put("uuid", GROUP_REF);
        group.put("users", users);

        groups.add(group);

        return groups;
    }

    private static RequestPacket prepareRawRequestPacket() {
        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("CPU", CPU_NUM);
        requestParams.put("Disk", DISK_SIZE);

        LinkedHashMap<String, Object> content = new LinkedHashMap<String, Object>();
        content.put("product", PRODUCT);
        content.put("params", requestParams);

        LinkedHashMap<String, Object> context = new LinkedHashMap<String, Object>();
        context.put("original_url", ORIGINAL_URL);
        LinkedHashMap<String, Object> headers = new LinkedHashMap<String, Object>();
        headers.put("x-rh-identity", ENCODED_USER);
        context.put("headers", headers);

        HashMap<String, Object> packetMap = new HashMap<String, Object>();
        packetMap.put("context", context);
        packetMap.put("content", content);

        return new RequestPacket(packetMap);
    }

    public static String CPU_NUM = "4";
    public static String DISK_SIZE = "100GB";
    public static String PRODUCT = "TESTING_PRODUCT";
    public static String ORIGINAL_URL = "TESTING_URL";
    public static String REQUEST_NAME = "TESTING_REQUEST_NAME";
    public static String REQUESTER = "TESTING_REQUESTER";
    public static String ID = "TESTING_ID";
    public static String PARENT_ID = "TESTING_PARENT_ID";
    public static String TENANT_ID = "TESTING_ID";
    public static String CREATED_AT = "2019-05-01T14:08:06.377Z";
    public static String ENCODED_USER = "eyJpZGVudGl0eSI6eyJhY2NvdW50X251bWJlciI6IjAzNjkyMzMiLCJ0eXBlIjoiVXNlciIsInVzZXIiOnsidXNlcm5hbWUiOiJqZG9lIiwiZW1haWwiOiJqZG9lQGFjbWUuY29tIiwiZmlyc3RfbmFtZSI6IkpvaG4iLCJsYXN0X25hbWUiOiJEb2UiLCJpc19hY3RpdmUiOnRydWUsImlzX29yZ19hZG1pbiI6ZmFsc2UsImlzX2ludGVybmFsIjpmYWxzZSwibG9jYWxlIjoiZW5fVVMifSwiaW50ZXJuYWwiOnsib3JnX2lkIjoiMzM0MDg1MSIsImF1dGhfdHlwZSI6ImJhc2ljLWF1dGgiLCJhdXRoX3RpbWUiOjYzMDB9fX0";
    public static String ENCODED_SYSADMIN = "x-rh-identity=eyJpZGVudGl0eSI6eyJhY2NvdW50X251bWJlciI6IjAzNjkyMzMiLCJ0eXBlIjoiVXNlciIsInVzZXIiOnsidXNlcm5hbWUiOiJzeXNhZG1pbiIsImVtYWlsIjoic3lzYWRtaW4iLCJmaXJzdF9uYW1lIjoic3lzYWRtaW4iLCJsYXN0X25hbWUiOiJzeXNhZG1pbiIsImlzX2FjdGl2ZSI6dHJ1ZSwiaXNfb3JnX2FkbWluIjpmYWxzZSwiaXNfaW50ZXJuYWwiOmZhbHNlLCJsb2NhbGUiOiJlbl9VUyJ9LCJpbnRlcm5hbCI6eyJvcmdfaWQiOiIzMzQwODUxIiwiYXV0aF90eXBlIjoiYmFzaWMtYXV0aCIsImF1dGhfdGltZSI6NjMwMH19LCJ1c2VyIjp7InVzZXJuYW1lIjoic3lzYWRtaW4iLCJlbWFpbCI6InN5c2FkbWluIiwiZmlyc3RfbmFtZSI6InN5c2FkbWluIiwibGFzdF9uYW1lIjoic3lzYWRtaW4iLCJpc19hY3RpdmUiOnRydWUsImlzX29yZ19hZG1pbiI6ZmFsc2UsImlzX2ludGVybmFsIjpmYWxzZSwibG9jYWxlIjoiZW5fVVMifX0";
    public static String RANDOM_ACCESS_KEY = "TESTING_RANDOM_ACCESS_KEY";
    public static String GROUP_REF = "TESTING_GROUP_UUID";
    public static String GROUP_NAME = "TESTING_GROUP_NAME";
    public static String FIRST_USERNAME = "TESTING_FIRST_USERNAME";
    public static String FIRST_FNAME = "TESTING_FNAME";
    public static String FIRST_LNAME = "TESTING_LNAME";
    public static String FIRST_EMAIL = "TESTING_FIRST_EMAIL";
}
