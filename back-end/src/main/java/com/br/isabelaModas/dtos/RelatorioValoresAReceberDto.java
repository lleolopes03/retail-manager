package com.br.isabelaModas.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioValoresAReceberDto {

    private String clienteNome;
    private String clienteCpf;
    private BigDecimal valor;
    private LocalDate dataVencimento;

    public RelatorioValoresAReceberDto(String clienteNome, String clienteCpf, BigDecimal valor, LocalDate dataVencimento) {
        this.clienteNome = clienteNome;
        this.clienteCpf = clienteCpf;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
    }

    public String getClienteNome() { return clienteNome; }
    public String getClienteCpf() { return clienteCpf; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
}


