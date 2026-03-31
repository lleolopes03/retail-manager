package com.br.isabelaModas.dtos;

import com.br.isabelaModas.entity.enums.StatusPagamento;

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
