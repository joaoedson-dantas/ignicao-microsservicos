package com.algaworks.algadelivery.courier.management.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.*;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter(AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Courier {

    @EqualsAndHashCode.Include
    @Id
    private UUID id;

    @Setter(AccessLevel.PUBLIC)
    private String name;

    @Setter(AccessLevel.PUBLIC)
    private String phone;

    private Integer fulFilledDeliveriesQuantity;

    private Integer pendingDeliveriesQuantity;

    private OffsetDateTime lastFulFilledDeliveryAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "courier")
    private List<AssignedDelivery> pendingDeliveries = new ArrayList<>();

    public List<AssignedDelivery> getPendingDeliveries() {
        return Collections.unmodifiableList(this.pendingDeliveries);
    }

    public static Courier brandNew(String name, String phone) {
        Courier courier = new Courier();

        courier.setId(UUID.randomUUID());
        courier.setName(name);
        courier.setPhone(phone);
        courier.setFulFilledDeliveriesQuantity(0);
        courier.setFulFilledDeliveriesQuantity(0);
        courier.setPendingDeliveriesQuantity(0);

        return courier;
    }

    // Atribuindo a entrega para este entregador
    public void assign(UUID deliveryId) {
        this.pendingDeliveries.add(
                AssignedDelivery.pending(deliveryId, this)
        );

        // Mantendo umas das invariantes
        this.setPendingDeliveriesQuantity(
                getPendingDeliveriesQuantity() + 1
        );
    }

    // Completando a entrega
    public void fulfill(UUID deliveryId) {
        AssignedDelivery delivery = this.pendingDeliveries.stream()
                .filter(d -> d.getId().equals(deliveryId))
                .findFirst()
                .orElseThrow();

        this.pendingDeliveries.remove(delivery);
        this.pendingDeliveriesQuantity--;
        this.fulFilledDeliveriesQuantity++;
        this.lastFulFilledDeliveryAt = OffsetDateTime.now();
    }
}
