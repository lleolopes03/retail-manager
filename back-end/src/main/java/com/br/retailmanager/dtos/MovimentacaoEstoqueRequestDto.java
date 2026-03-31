package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.enums.TipoMovimentacao;

public class MovimentacaoEstoqueRequestDto {
    private Long produtoId;
    private TipoMovimentacao tipoMovimentacao;
    private Integer quantidade;
    private String observacao;
    private Long vendaId;   // opcional, se vinculado a uma venda
    private Long clienteId; // opcional, se vinculado a um cliente (experimentação)

    public MovimentacaoEstoqueRequestDto() {
    }

    public MovimentacaoEstoqueRequestDto(Long produtoId, TipoMovimentacao tipoMovimentacao, Integer quantidade, String observacao, Long vendaId, Long clienteId) {
        this.produtoId = produtoId;
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.vendaId = vendaId;
        this.clienteId = clienteId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}
