package com.br.isabelaModas.dtos;

import java.math.BigDecimal;

public class RelatorioClienteDto {
    private String nomeCliente;
    private String cpf;
    private BigDecimal totalComprado;

    public RelatorioClienteDto(String nomeCliente, String cpf, BigDecimal totalComprado) {
        this.nomeCliente = nomeCliente;
        this.cpf = cpf;
        this.totalComprado = totalComprado;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getCpf() {
        return cpf;
    }

    public BigDecimal getTotalComprado() {
        return totalComprado;
    }


}
