package com.algaworks.algadelivery.delivery.tracking.domain.repository;

import com.algaworks.algadelivery.delivery.tracking.domain.model.ContactPoint;
import com.algaworks.algadelivery.delivery.tracking.domain.model.Delivery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Basicamente adiciona Beans de persistência -> Ou seja, ele já carrega o repository
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Para não tentar criar um banco de dados de teste
class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    public void shouldPersist() {
        Delivery delivery = Delivery.draft();

        delivery.editPreparationDetails(createdValidPreparationDetails());

        delivery.addItem("Computador", 2);
        delivery.addItem("Notebook", 2);

        // Persistindo
        deliveryRepository.saveAndFlush(delivery);

        // Recuperando
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId())
                .orElseThrow();

        assertNotNull(delivery.getId());
        assertEquals(2, persistedDelivery.getItems().size());

    }

    private Delivery.PreparationDetails createdValidPreparationDetails() {
        ContactPoint sender = ContactPoint.builder()
                .zipCode("60541195")
                .street("Rua Teodoro de Castro")
                .number("100")
                .complement("Ap.01")
                .name("João Edson")
                .phone("85984420772")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("62736000")
                .street("Rua do Travessa do Amor")
                .number("196")
                .complement("")
                .name("Maria Fabriciane Silva Costa")
                .phone("85984420188")
                .build();

        return Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("5.00"))
                .expectedDeliveryTime(Duration.ofHours(5))
                .build();
    }
}