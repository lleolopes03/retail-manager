package com.br.isabelaModas.dtos;

import com.br.isabelaModas.entity.Categoria;

import java.math.BigDecimal;

public class ProdutoRequestDto {
    private String nome;
    private BigDecimal preco;
    private String tamanho;
    private String cor;
    private int estoqueAtual;
    private Long categoriaId;

    public ProdutoRequestDto() {
    }

    public ProdutoRequestDto(String nome, BigDecimal preco, String tamanho, String cor, int estoqueAtual, Long categoriaId) {
        this.nome = nome;
        this.preco = preco;
        this.tamanho = tamanho;
        this.cor = cor;
        this.estoqueAtual = estoqueAtual;
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public int getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(int estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
