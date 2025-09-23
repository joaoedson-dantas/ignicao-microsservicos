package com.algaworks.algadelivery.courier.management.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourierTest {

    @Test
    public void itShouldBePossibleToCreateACourier() {
        Courier courier = Courier.brandNew("Léia Bernardo", "8534989319");

        assertNotNull(courier.getId());
        assertEquals("Léia Bernardo", courier.getName());
        assertEquals("8534989319", courier.getPhone());
        assertEquals(0, (int) courier.getPendingDeliveriesQuantity());
        assertTrue(courier.getPendingDeliveries().isEmpty());
    }
}