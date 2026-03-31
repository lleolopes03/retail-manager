package com.br.isabelaModas.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VendaResponseDto {
    private Long id;
    private LocalDate dataVenda;
    private BigDecimal valorTotal;
    private LocalDate dataVencimento;
    private String statusPagamento;
    private String tipoPagamento;   // precisa ser preenchido
    private Integer numeroParcelas; // precisa ser preenchido
    private ClienteResponseDto cliente;
    private List<ItemVendaDto> itens;

    // 🔹 novos campos para carnê
    private LocalDate primeiraParcela;
    private Integer intervaloDias;

    private FormaPagamentoResponseDto formaPagamento;






    public VendaResponseDto() {
    }

    public VendaResponseDto(Long id, LocalDate dataVenda, BigDecimal valorTotal, LocalDate dataVencimento, String statusPagamento, String tipoPagamento, Integer numeroParcelas, ClienteResponseDto cliente, List<ItemVendaDto> itens, LocalDate primeiraParcela, Integer intervaloDias, FormaPagamentoResponseDto formaPagamento) {

        this.id = id;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.dataVencimento = dataVencimento;
        this.statusPagamento = statusPagamento;
        this.tipoPagamento = tipoPagamento;
        this.numeroParcelas = numeroParcelas;
        this.cliente = cliente;
        this.itens = itens;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
        this.formaPagamento = formaPagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public ClienteResponseDto getCliente() {
        return cliente;
    }

    public void setCliente(ClienteResponseDto cliente) {
        this.cliente = cliente;
    }

    public List<ItemVendaDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaDto> itens) {
        this.itens = itens;
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

    public FormaPagamentoResponseDto getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamentoResponseDto formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
