package com.br.retailmanager.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompraResponseDto {
    private Long id;
    private LocalDate dataCompra;
    private BigDecimal valorTotal;
    private FornecedorResponseDto fornecedor;
    private List<ItemCompraDto> itens;

    public CompraResponseDto() {
    }

    public CompraResponseDto(Long id, LocalDate dataCompra, BigDecimal valorTotal, FornecedorResponseDto fornecedor, List<ItemCompraDto> itens) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.valorTotal = valorTotal;
        this.fornecedor = fornecedor;
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public FornecedorResponseDto getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorResponseDto fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<ItemCompraDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompraDto> itens) {
        this.itens = itens;
    }
}
