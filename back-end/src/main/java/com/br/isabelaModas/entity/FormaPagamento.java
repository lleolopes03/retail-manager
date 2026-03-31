package com.br.isabelaModas.entity;

import com.br.isabelaModas.entity.enums.TipoPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class FormaPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoPagamento tipo; // CREDITO, DEBITO, DINHEIRO, CARNE, PIX

    private Integer numeroParcelas; // usado para crédito, carnê, pix parcelado
    private LocalDate primeiraParcela; // usado para carnê
    private Integer intervaloDias; // usado para carnê (ex.: 30 dias entre parcelas)

    private String linkPagamento; // usado para PIX/link
    private BigDecimal valorTotal;

    @OneToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;

    public FormaPagamento() {
    }

    public FormaPagamento(Long id, TipoPagamento tipo, Integer numeroParcelas, LocalDate primeiraParcela, Integer intervaloDias, String linkPagamento, BigDecimal valorTotal, Venda venda) {
        this.id = id;
        this.tipo = tipo;
        this.numeroParcelas = numeroParcelas;
        this.primeiraParcela = primeiraParcela;
        this.intervaloDias = intervaloDias;
        this.linkPagamento = linkPagamento;
        this.valorTotal = valorTotal;
        this.venda = venda;
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

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FormaPagamento that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FormaPagamento{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", numeroParcelas=" + numeroParcelas +
                ", primeiraParcela=" + primeiraParcela +
                ", intervaloDias=" + intervaloDias +
                ", linkPagamento='" + linkPagamento + '\'' +
                ", valorTotal=" + valorTotal +
                ", venda=" + venda +
                '}';
    }
}
