package com.webcodein.locking.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobTicketEntityTest {

    @Test
    @DisplayName("Should create job ticket and update status")
    void createAndUpdateJobTicket() {
        JobTicket ticket = new JobTicket("PROCESS_IMAGE_42", "PENDING");
        ticket.setId(10L);
        ticket.setVersion(0L);

        assertEquals(10L, ticket.getId());
        assertEquals("PROCESS_IMAGE_42", ticket.getPayload());
        assertEquals("PENDING", ticket.getStatus());
        assertEquals(0L, ticket.getVersion());

        ticket.setStatus("PROCESSING");
        assertEquals("PROCESSING", ticket.getStatus());

        ticket.setPayload("PROCESS_IMAGE_42_RETRY");
        assertEquals("PROCESS_IMAGE_42_RETRY", ticket.getPayload());
    }

    @Test
    @DisplayName("Should instantiate empty constructor correctly")
    void emptyConstructor() {
        JobTicket ticket = new JobTicket();
        assertNull(ticket.getId());
        assertNull(ticket.getPayload());
        assertNull(ticket.getStatus());
    }
}
