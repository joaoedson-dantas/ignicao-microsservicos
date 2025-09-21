package com.algaworks.algadelivery.delivery.tracking.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// No DDD -> A Entidade deve ter uma identidade única,
// por conta disso é necessário adicionar EqualsAndHashCode para comparação apenas via identificador (ID ou UUID)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter(AccessLevel.PRIVATE) // todos privados, utilizando dentro de métodos dentro da própria classe.
@Getter
public class Delivery {

    @EqualsAndHashCode.Include
    private UUID id;

    private UUID courierId;

    private DeliveryStatus status;
    private List<Item> items = new ArrayList<>();
    private ContactPoint sender;
    private ContactPoint recipient;

    private OffsetDateTime placedAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    private Integer totalItems;

    // Static Factories
    // Nesse caso estaríamos criando uma delivery no status/estagio de draft(rascunho).
    public static Delivery draft() {
        // Como é uma factory para o domínio, ela também vai poder encapsular regras de negócio,
        // como a de inicialização desse objeto.
        Delivery delivery = new Delivery();

        // regras de valores padrões para instânciar o objeto
        delivery.setId(UUID.randomUUID());
        delivery.setStatus(DeliveryStatus.DRAFT);
        delivery.setTotalCost(BigDecimal.ZERO);
        delivery.setTotalItems(0);
        delivery.setCourierPayout(BigDecimal.ZERO);
        delivery.setDistanceFee(BigDecimal.ZERO);

        return delivery;
    }

    public List<Item> getItems() {
        // funciona como uma visão somente para leitura (read-only view) de uma lista existente,
        // que por sua vez pode ser mutável ou imutável.
        // Ajuda a manter a regra do DDD relacionado ao Aggregate, pois quem tiver acesso a essa lista
        // Não deve conseguir modificar, apenas o Aggregate Root deve conseguir.
        return Collections.unmodifiableList(this.items);
    }
}
