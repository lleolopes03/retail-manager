package com.br.isabelaModas.dtos;

import java.math.BigDecimal;

public class ProdutoResponseDto {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private String tamanho;
    private String cor;
    private int estoqueAtual;
    private CategoriaResponseDto categoria;

    public ProdutoResponseDto() {
    }

    public ProdutoResponseDto(Long id, String nome, BigDecimal preco, String tamanho, String cor, int estoqueAtual, CategoriaResponseDto categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.tamanho = tamanho;
        this.cor = cor;
        this.estoqueAtual = estoqueAtual;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CategoriaResponseDto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDto categoria) {
        this.categoria = categoria;
    }
}
