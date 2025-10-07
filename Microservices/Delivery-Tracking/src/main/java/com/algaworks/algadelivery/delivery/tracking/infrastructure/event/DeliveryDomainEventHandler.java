package com.algaworks.algadelivery.delivery.tracking.infrastructure.event;

import com.algaworks.algadelivery.delivery.tracking.domain.event.DeliveryFulfiledEvent;
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

    // Métodos que irão fazer as escutas dos eventos

    @EventListener // Informando para o spring que desejo realizar a leitura do evento
    // o Spring automaticamente vai executar esse método assim que o DeliveryPlacedEvent for lançado
    public void handle(DeliveryPlacedEvent placedEvent) {
        log.info(placedEvent.toString());
    }

    @EventListener
    public void handle(DeliveryPickUpEvent pickUpEvent) {
        log.info(pickUpEvent.toString());
    }

    @EventListener
    public void handle(DeliveryFulfiledEvent fulfiledEvent) {
        log.info(fulfiledEvent.toString());
    }

}
