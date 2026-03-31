package com.br.retailmanager.dtos;

import java.math.BigDecimal;

public record RelatorioFormaPagamentoDto(
        String tipo,           // 🔹 String, NÃO TipoPagamento enum
        BigDecimal total
) {
}
