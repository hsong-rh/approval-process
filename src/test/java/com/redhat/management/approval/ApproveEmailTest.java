package com.redhat.management.approval;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class ApproveEmailTest {
    public ArrayList<String> recipients = new ArrayList<String>();

    @Test
    public void testGetSubjectTest() {
        ApproveEmail email = new ApproveEmail(recipients);
        String subject = email.getSubject();

        assertEquals(subject, "Catalog : Approval Order ");
    }
}

