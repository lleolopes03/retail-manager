package com.br.retailmanager.dtos;

import com.br.retailmanager.entity.Endereco;
import jakarta.persistence.Embedded;

public class FornecedorRequestDto {
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    @Embedded
    private EnderecoDto endereco;

    public FornecedorRequestDto() {
    }

    public FornecedorRequestDto(String nome, String cnpj, String email, String telefone, EnderecoDto endereco) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public EnderecoDto getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDto endereco) {
        this.endereco = endereco;
    }
}
