package com.br.isabelaModas.dtos;

public class ProdutoEstoqueDto {
    private String nome;
    private String tamanho;
    private int quantidadeAtual;

    public ProdutoEstoqueDto(String nome, String tamanho, int quantidadeAtual) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.quantidadeAtual = quantidadeAtual;
    }

    public String getNome() { return nome; }
    public String getTamanho() { return tamanho; }
    public int getQuantidadeAtual() { return quantidadeAtual; }

}
