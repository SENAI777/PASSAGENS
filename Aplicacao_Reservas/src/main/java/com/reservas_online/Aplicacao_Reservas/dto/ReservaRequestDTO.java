package com.reservas_online.Aplicacao_Reservas.dto;

import java.math.BigDecimal;

public record ReservaRequestDTO(
        Long codPassageiro,
        Long idPassageiro,
        Long codVoo,
        Long idVoo,
        BigDecimal tarifa,
        Integer qtdBagagens
) {
    public Long obterIdPassageiro() {
        return codPassageiro != null ? codPassageiro : idPassageiro;
    }

    public Long obterIdVoo() {
        return codVoo != null ? codVoo : idVoo;
    }
}
