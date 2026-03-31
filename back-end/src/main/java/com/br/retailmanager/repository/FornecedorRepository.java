package com.br.retailmanager.repository;

import com.br.retailmanager.entity.Fornecedor;
import com.br.retailmanager.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor,Long> {
    Optional<Fornecedor> findByNome(String nome);
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
    Optional<Fornecedor> findByCnpj(String cnpj);
    Optional<Fornecedor> findByEmail(String email);
    boolean existsByCnpj(String cnpj); // útil para validar duplicidade

    boolean existsByEmail(String email); // útil para validar duplicidade

}
