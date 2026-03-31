package com.br.isabelaModas.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public class EmpresaRequestDto {
    @NotBlank
    private String nome;


    @CNPJ
    private String cnpj;

    @Email
    private String email;

    @NotBlank
    private String telefone;

    private EnderecoDto Endereco;

    private int numero;

    public EmpresaRequestDto() {
    }

    public EmpresaRequestDto(String nome, String cnpj, String email, String telefone, EnderecoDto endereco, int numero) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        Endereco = endereco;
        this.numero = numero;
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
        return Endereco;
    }

    public void setEndereco(EnderecoDto endereco) {
        Endereco = endereco;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
