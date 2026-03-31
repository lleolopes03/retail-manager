package com.br.isabelaModas.security;

import com.br.isabelaModas.entity.Funcionario;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetail extends User {

    private Funcionario funcionario;
    public JwtUserDetail(Funcionario funcionario){
        super(funcionario.getLogin(), funcionario.getSenha(),
                AuthorityUtils.createAuthorityList("ROLE_" + funcionario.getPerfil().name()));

    }
    public Long getId(){
        return  this.funcionario.getId();
    }
    public String getRole(){
        return this.funcionario.getPerfil().name();
    }
}
