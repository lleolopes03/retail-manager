package com.br.retailmanager.dtos;

import java.time.LocalDate;
import java.util.List;

public class CompraRequestDto {
    private LocalDate dataCompra;
    private Long fornecedorId;
    private List<ItemCompraDto> itens;

    public CompraRequestDto() {
    }

    public CompraRequestDto(LocalDate dataCompra, Long fornecedorId, List<ItemCompraDto> itens) {
        this.dataCompra = dataCompra;
        this.fornecedorId = fornecedorId;
        this.itens = itens;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Long getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(Long fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    public List<ItemCompraDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompraDto> itens) {
        this.itens = itens;
    }
}
