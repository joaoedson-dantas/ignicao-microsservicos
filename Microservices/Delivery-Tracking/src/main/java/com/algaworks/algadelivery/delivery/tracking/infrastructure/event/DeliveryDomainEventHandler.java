package com.algaworks.algadelivery.delivery.tracking.infrastructure.event;

import com.algaworks.algadelivery.delivery.tracking.domain.event.DeliveryFulfilledEvent;
import com.algaworks.algadelivery.delivery.tracking.domain.event.DeliveryPickUpEvent;
import com.algaworks.algadelivery.delivery.tracking.domain.event.DeliveryPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j // log do lombok
@RequiredArgsConstructor
public class DeliveryDomainEventHandler {

    public static final String DELIVERIES_EVENTS_TOPIC_NAME = "deliveries.v1.events";
    private final IntegrationEventPublisher integrationEventPublisher;


    // Métodos que irão fazer as escutas dos eventos

    @EventListener // Informando para o spring que desejo realizar a leitura do evento
    // o Spring automaticamente vai executar esse método assim que o DeliveryPlacedEvent for lançado
    public void handle(DeliveryPlacedEvent placedEvent) {
        log.info(placedEvent.toString());
        // convertendo eventos de domínios em eventos de integração
        integrationEventPublisher.publish(placedEvent, placedEvent.getDeliveryId().toString(),
                DELIVERIES_EVENTS_TOPIC_NAME);
    }

    @EventListener
    public void handle(DeliveryPickUpEvent pickUpEvent) {
        log.info(pickUpEvent.toString());
        integrationEventPublisher.publish(pickUpEvent, pickUpEvent.getDeliveryId().toString(),
                DELIVERIES_EVENTS_TOPIC_NAME);
    }

    @EventListener
    public void handle(DeliveryFulfilledEvent fulfiledEvent) {
        log.info(fulfiledEvent.toString());
        integrationEventPublisher.publish(fulfiledEvent, fulfiledEvent.getDeliveryId().toString(),
                DELIVERIES_EVENTS_TOPIC_NAME);
    }

}
