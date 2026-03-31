package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.Endereco;

import java.time.LocalDate;

public class ClienteResponseDto {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String email;
    private String telefone;
    private Endereco endereco;
    // ✅ campo calculado dinamicamente
    private int idade;

    // ✅ opcional: flag para saber se hoje é aniversário
    private boolean aniversarioHoje;

    public ClienteResponseDto() {
    }

    public ClienteResponseDto(Long id, String nome, LocalDate dataNascimento, String cpf, String email, String telefone, Endereco endereco, int idade, boolean aniversarioHoje) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.idade = idade;
        this.aniversarioHoje = aniversarioHoje;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public boolean isAniversarioHoje() {
        return aniversarioHoje;
    }

    public void setAniversarioHoje(boolean aniversarioHoje) {
        this.aniversarioHoje = aniversarioHoje;
    }
}
