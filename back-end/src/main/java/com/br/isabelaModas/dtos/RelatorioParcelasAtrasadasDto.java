package com.br.isabelaModas.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioParcelasAtrasadasDto(
        String clienteNome,
        String clienteCpf,
        Integer numero,
        BigDecimal valor,
        LocalDate dataVencimento,
        Long diasAtraso
) {
}
