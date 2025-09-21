package com.algaworks.algadelivery.delivery.tracking.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// Var ser um VO  (Value Object) -> Não precisa de Setter, ele tem que ser instânciado com todos os parâmetros
// Não deve ter modificações desses parâmetros.
// Seguindo o DDD -> Ele deve ser comparado utilizando todos os valores disponíveis
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Getter
public class ContactPoint {
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String name;
    private String phone;
}
