package com.br.isabelaModas.dtos;

import java.time.LocalDate;
import java.util.List;
public class VendaRequestDto {
    private LocalDate dataVenda;
    private LocalDate dataVencimento;
    private String statusPagamento; // PAGO, PENDENTE, etc.
    private Long clienteId;
    private List<ItemVendaDto> itens;

    // 🔹 forma de pagamento
    private String tipoPagamento;      // CREDITO, DEBITO, DINHEIRO, PIX, CARNE
    private Integer numeroParcelas;    // usado para crédito/carnê/pix parcelado

    // 🔹 campos específicos para carnê
    private LocalDate primeiraParcela; // data da primeira parcela
    private Integer intervaloDias;


    public VendaRequestDto() {
    }

    public VendaRequestDto(LocalDate dataVenda, LocalDate dataVencimento, String statusPagamento, Long clienteId, List<ItemVendaDto> itens, String tipoPagamento, Integer numeroParcelas, LocalDate primeiraParcela, Integer intervaloDias) {
        this.dataVenda = dataVenda;
        this.dataVencimento = dataVencimento;
        this.statusPagamento = statusPagamento;
        this.clienteId = clienteId;
        this.itens = itens;
        this.tipoPagamento = tipoPagamento;
        this.numeroParcelas = numeroParcelas;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemVendaDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaDto> itens) {
        this.itens = itens;
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

}





