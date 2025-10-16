package com.algaworks.algadelivery.courier.management.infrastructure.kafka;

import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent;
import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

// TODAS AS MENSAGENS VÃO PARAR NESSA CLASSE PRIMEIRO (PORTA DE ENTRADA)
@Component
@KafkaListener(topics = { // Essa anotação realiza a leitura de um tópico ou mais que um, ele vai ler todas as mensagens desse tópico
        "deliveries.v1.events"
}, groupId = "courier-management")
@Slf4j
@RequiredArgsConstructor
public class KafkaDeliveriesMessageHandler {

    @KafkaHandler(isDefault = true)
    public void defaultHandler(@Payload Object object) {
        log.info("Default Handler: {}", object);
    }


    // MÉTODOS PARA FAZEREM A LEITURA DOS INTEGRATION-EVENTS
    @KafkaHandler
    public void handle(@Payload DeliveryPlacedIntegrationEvent event) {
        log.info("Received: {}", event);
    }

    @KafkaHandler
    public void handle(@Payload DeliveryFulfilledIntegrationEvent event) {
        log.info("Received: {}", event);
    }
}
