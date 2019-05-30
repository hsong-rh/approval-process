package com.redhat.management.approval;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class EmailTest {
    public List<String> recipients = new ArrayList<String>();

    @Test
    public void testGetSubjectTest() {
        Email email = new Email(recipients);
        String subject = email.getSubject();

        assertEquals(subject, "Catalog : Approval Order ");
    }
}

