package com.br.isabelaModas.entity;

import com.br.isabelaModas.entity.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Parcela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero; // nº da parcela (1, 2, 3...)
    private BigDecimal valor;
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status; // PENDENTE, PAGO, ATRASADO

    @ManyToOne
    private Venda venda;

    public Parcela() {
    }

    public Parcela(Long id, int numero, BigDecimal valor, LocalDate dataVencimento, StatusPagamento status, Venda venda) {
        this.id = id;
        this.numero = numero;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.venda = venda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Parcela parcela)) return false;
        return Objects.equals(id, parcela.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Parcela{" +
                "id=" + id +
                ", numero=" + numero +
                ", valor=" + valor +
                ", dataVencimento=" + dataVencimento +
                ", status=" + status +
                ", venda=" + venda +
                '}';
    }
}
