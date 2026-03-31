package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.enums.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FormaPagamentoRequestDto {
    private Long vendaId;
    private TipoPagamento tipo;          // CREDITO, DEBITO, DINHEIRO, CARNE, PIX
    private Integer numeroParcelas;      // usado para crédito/carnê/pix parcelado
    private LocalDate primeiraParcela;   // usado para carnê
    private Integer intervaloDias;       // usado para carnê
    private String linkPagamento;        // usado para PIX/link
    private BigDecimal valorTotal;       // valor total da venda

    public FormaPagamentoRequestDto() {
    }

    public FormaPagamentoRequestDto(Long vendaId, TipoPagamento tipo, Integer numeroParcelas, LocalDate primeiraParcela, Integer intervaloDias, String linkPagamento, BigDecimal valorTotal) {
        this.vendaId = vendaId;
        this.tipo = tipo;
        this.numeroParcelas = numeroParcelas;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
        this.linkPagamento = linkPagamento;
        this.valorTotal = valorTotal;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
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
