package com.br.retailmanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioInadimplenciaGeralDto(
        String clienteNome,
        String clienteCpf,
        String tipo,              // "VENDA" ou "PARCELA"
        String descricao,         // "Venda #123" ou "Parcela 2/5"
        BigDecimal valor,
        LocalDate dataVencimento,
        Long diasAtraso
) {
}
