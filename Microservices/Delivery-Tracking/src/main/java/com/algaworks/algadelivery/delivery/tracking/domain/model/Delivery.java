package com.algaworks.algadelivery.delivery.tracking.domain.model;

import com.algaworks.algadelivery.delivery.tracking.domain.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
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
@Entity
public class Delivery {

    @EqualsAndHashCode.Include
    @Id
    private UUID id;

    private UUID courierId;
    private DeliveryStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode", column = @Column(name = "sender_zip_code")),
            @AttributeOverride(name = "street", column = @Column(name = "sender_street")),
            @AttributeOverride(name = "number", column = @Column(name = "sender_number")),
            @AttributeOverride(name = "complement", column = @Column(name = "sender_complement")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "sender_zip_code")),
            @AttributeOverride(name = "name", column = @Column(name = "sender_name")),
            @AttributeOverride(name = "phone", column = @Column(name = "sender_phone"))
    })
    private ContactPoint sender;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode", column = @Column(name = "recipient_zip_code")),
            @AttributeOverride(name = "street", column = @Column(name = "recipient_street")),
            @AttributeOverride(name = "number", column = @Column(name = "recipient_number")),
            @AttributeOverride(name = "complement", column = @Column(name = "recipient_complement")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "recipient_zip_code")),
            @AttributeOverride(name = "name", column = @Column(name = "recipient_name")),
            @AttributeOverride(name = "phone", column = @Column(name = "recipient_phone"))
    })
    private ContactPoint recipient;

    private OffsetDateTime placedAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    // Invariant que precisa ser protegida.
    private Integer totalItems;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "delivery") // -> Gerando uma relação bidirecional |
    // cascade -> Como é uma aggregate e será persistido de uma única vez, ou seja, assim que persistir o delivery
    // o JakartaPersistence vai persistir os itens
    private List<Item> items = new ArrayList<>();

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

    // DDD -> Apenas o AggregateRoot pode modificar a lista de itens.
    // A ideia é garantir as invariantes, como o cálculo do valor total dos itens,
    // que precisam ser baseado nos itens que estão contidos na Delivery.
    public UUID addItem(String name, int quantity) {
        Item item = Item.brandNew(name, quantity, this);
        items.add(item);
        this.calculateTotalItems();
        return item.getId();
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        this.calculateTotalItems();
    }

    public void removeItems() {
        items.clear();
        this.calculateTotalItems();
    }

    // Alterando entidades num Aggregator.
    public void changeItemQuantity(UUID itemId, int quantity) {
        Item item = getItems().stream().filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow();

        item.setQuantity(quantity);
        calculateTotalItems();
    }

    public List<Item> getItems() {
        // Funciona como uma visão somente para leitura (read-only view) de uma lista existente,
        // que por sua vez pode ser mutável ou imutável.
        // Ajuda a manter a regra do DDD relacionado ao Aggregate, pois quem tiver acesso a essa lista
        // Não deve conseguir modificar, apenas o Aggregate Root deve conseguir.
        return Collections.unmodifiableList(this.items);
    }

    public void editPreparationDetails(PreparationDetails details) {
        verifyIfCanBeEdited();

        setSender(details.getSender());
        setRecipient(details.getRecipient());
        setDistanceFee(details.getDistanceFee());
        setCourierPayout(details.getCourierPayout());

        // Regra para calcular o prazo de entrega
        setExpectedDeliveryAt(OffsetDateTime.now().plus(details.getExpectedDeliveryTime()));
        setTotalCost(this.getDistanceFee().add(this.getCourierPayout()));
    }

    public void place() {
        // Regras de negócio
        verifyIfCanBePlaced();

        this.changeStatusTo(DeliveryStatus.WAITING_FOR_COURIER);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void pickUp(UUID courierId) {
        this.setCourierId(courierId);
        this.changeStatusTo(DeliveryStatus.IN_TRANSIT);
        this.setAssignedAt(OffsetDateTime.now());
    }

    public void markAsDelivered() {
        this.changeStatusTo(DeliveryStatus.DELIVERED);
        this.setFulfilledAt(OffsetDateTime.now());
    }

    private void calculateTotalItems() {
        // Leva em conta a quantidade que tem dentro de cada item e soma.
        int totalItems = getItems().stream().mapToInt(Item::getQuantity).sum();
        setTotalItems(totalItems);
    }

    private boolean isFilled() {
        return this.getSender() != null
                && this.getRecipient() != null
                && this.totalCost != null;
    }

    private void verifyIfCanBePlaced() {
        if (!isFilled()) {
            throw new DomainException();
        }

        if (!getStatus().equals(DeliveryStatus.DRAFT)) {
            throw new DomainException();
        }
    }

    private void verifyIfCanBeEdited() {
        if (!getStatus().equals(DeliveryStatus.DRAFT)) {
            throw new DomainException();
        }
    }

    private void changeStatusTo(DeliveryStatus newStatus) {
        if (newStatus != null && this.getStatus().canNotChangeTo(newStatus)) {
            throw new DomainException(
                    "Invalid status transition from " + this.getStatus() +
                    " to " + newStatus
            );
        }
        this.setStatus(newStatus);
    }

    @Getter
    @AllArgsConstructor
    @Builder
    // Classe servirá apenas para passagem de parâmetro
    // Inner class
    public static class PreparationDetails {
        private ContactPoint sender;
        private ContactPoint recipient;
        private BigDecimal distanceFee;
        private BigDecimal courierPayout;
        private Duration expectedDeliveryTime;
    }
}
