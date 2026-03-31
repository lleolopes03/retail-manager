package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaDto(
        Long id,
        int numero,
        BigDecimal valor,
        LocalDate dataVencimento,
        StatusPagamento status,
        Long vendaId,
        String clienteNome
) {
}
