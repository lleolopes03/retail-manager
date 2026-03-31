package com.br.isabelaModas.entity;

import com.br.isabelaModas.entity.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataVenda;
    private BigDecimal valorTotal;
    private LocalDate dataVencimento;   // quando o pagamento vence

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento; // PAGO, PENDENTE, etc.

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToOne(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FormaPagamento formaPagamento;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // 🔹 campos já existentes
    private String tipoPagamento;     // CREDITO, DEBITO, PIX, DINHEIRO, CARNE
    private Integer numeroParcelas;   // usado para crédito/carnê

    // 🔹 novos campos para carnê
    private LocalDate primeiraParcela; // data da primeira parcela
    private Integer intervaloDias;     // intervalo entre parcelas (em dias)


    public Venda() {
    }

    public Venda(Long id, LocalDate dataVenda, BigDecimal valorTotal, LocalDate dataVencimento, StatusPagamento statusPagamento, List<ItemVenda> itens, FormaPagamento formaPagamento, Cliente cliente, String tipoPagamento, Integer numeroParcelas, LocalDate primeiraParcela, Integer intervaloDias) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.dataVencimento = dataVencimento;
        this.statusPagamento = statusPagamento;
        this.itens = itens;
        this.formaPagamento = formaPagamento;
        this.cliente = cliente;
        this.tipoPagamento = tipoPagamento;
        this.numeroParcelas = numeroParcelas;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
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

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Venda venda)) return false;
        return Objects.equals(id, venda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Venda{" +
                "id=" + id +
                ", dataVenda=" + dataVenda +
                ", valorTotal=" + valorTotal +
                ", itens=" + itens +
                ", formaPagamento=" + formaPagamento +
                '}';
    }
}
