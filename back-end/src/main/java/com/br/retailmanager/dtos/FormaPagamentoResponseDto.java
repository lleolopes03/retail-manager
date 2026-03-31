package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.enums.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FormaPagamentoResponseDto {
    private Long id;
    private TipoPagamento tipo;
    private Integer numeroParcelas;
    private LocalDate primeiraParcela;
    private Integer intervaloDias;
    private String linkPagamento;
    private BigDecimal valorTotal;

    public FormaPagamentoResponseDto() {}

    public FormaPagamentoResponseDto(Long id, TipoPagamento tipo, Integer numeroParcelas, LocalDate primeiraParcela, Integer intervaloDias, String linkPagamento, BigDecimal valorTotal) {
        this.id = id;
        this.tipo = tipo;
        this.numeroParcelas = numeroParcelas;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
        this.linkPagamento = linkPagamento;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPagamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoPagamento tipo) {
        this.tipo = tipo;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public LocalDate getPrimeiraParcela() {
        return primeiraParcela;
    }

    public void setPrimeiraParcela(LocalDate primeiraParcela) {
        this.primeiraParcela = primeiraParcela;
    }

    public Integer getIntervaloDias() {
        return intervaloDias;
    }

    public void setIntervaloDias(Integer intervaloDias) {
        this.intervaloDias = intervaloDias;
    }

    public String getLinkPagamento() {
        return linkPagamento;
    }

    public void setLinkPagamento(String linkPagamento) {
        this.linkPagamento = linkPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
