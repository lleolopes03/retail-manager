package com.br.retailmanager.repository;

import com.br.retailmanager.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Optional<Cliente> findByNome(String nome);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByEmail(String email);
    boolean existsByCpf(String cpf);



    // Buscar clientes que têm vendas registradas
    @Query("SELECT c FROM Cliente c JOIN c.vendas v WHERE v.dataVenda BETWEEN :inicio AND :fim")
    List<Cliente> findClientesComVendasNoPeriodo(LocalDate inicio, LocalDate fim);

    // Buscar clientes por CPF e período de vendas
    @Query("SELECT c FROM Cliente c JOIN c.vendas v WHERE c.cpf = :cpf AND v.dataVenda BETWEEN :inicio AND :fim")
    Optional<Cliente> findClientePorCpfEVendasNoPeriodo(String cpf, LocalDate inicio, LocalDate fim);



}
