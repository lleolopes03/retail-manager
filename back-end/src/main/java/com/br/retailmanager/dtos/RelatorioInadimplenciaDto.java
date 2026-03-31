package com.br.retailmanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioInadimplenciaDto {

    private String clienteNome;
    private String clienteCpf;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private long diasAtraso;

    public RelatorioInadimplenciaDto(String clienteNome, String clienteCpf, BigDecimal valor, LocalDate dataVencimento, long diasAtraso) {
        this.clienteNome = clienteNome;
        this.clienteCpf = clienteCpf;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.diasAtraso = diasAtraso;
    }

    public String getClienteNome() { return clienteNome; }
    public String getClienteCpf() { return clienteCpf; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public long getDiasAtraso() { return diasAtraso; }
}


