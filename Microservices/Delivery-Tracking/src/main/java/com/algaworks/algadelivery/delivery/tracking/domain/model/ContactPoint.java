package com.algaworks.algadelivery.delivery.tracking.domain.model;


import jakarta.persistence.Embeddable;
import lombok.*;

// Var ser um VO  (Value Object) -> Não precisa de Setter, ele tem que ser instânciado com todos os parâmetros
// Não deve ter modificações desses parâmetros.
@EqualsAndHashCode // Seguindo o DDD -> Ele deve ser comparado utilizando todos os valores disponíveis
@AllArgsConstructor
@Builder
@Getter
@Embeddable // propriedades compostas que não são entidades
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContactPoint {
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String name;
    private String phone;
}
