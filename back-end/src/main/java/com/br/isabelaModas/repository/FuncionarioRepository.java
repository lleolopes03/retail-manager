package com.br.isabelaModas.repository;


import com.br.isabelaModas.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {
    Optional<Funcionario> findByNome(String nome);
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
    Optional<Funcionario> findByCpf(String cpf);
    Optional<Funcionario> findByEmail(String email);
    Optional<Funcionario> findByLogin(String login);



}
