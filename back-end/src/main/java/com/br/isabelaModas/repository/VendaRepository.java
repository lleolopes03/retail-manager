package com.br.isabelaModas.repository;

import com.br.isabelaModas.entity.Venda;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.entity.enums.TipoPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Busca por CPF com paginação
    Page<Venda> findByClienteCpf(String cpf, Pageable pageable);

    // Busca por nome com paginação
    Page<Venda> findByClienteNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Venda> findByDataVendaBetween(LocalDate inicio, LocalDate fim, PageRequest pageRequest);

    // Busca por CPF e intervalo de datas com paginação
    Page<Venda> findByClienteCpfAndDataVendaBetween(String cpf, LocalDate inicio, LocalDate fim, Pageable pageable);

    // Relatórios
    @Query("SELECT SUM(v.valorTotal) FROM Venda v WHERE v.dataVenda = :data")
    BigDecimal totalVendasDoDia(LocalDate data);

    @Query("SELECT SUM(v.valorTotal) FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim")
    BigDecimal totalVendasNoPeriodo(LocalDate inicio, LocalDate fim);



    @Query("SELECT v.cliente.nome, v.cliente.cpf, SUM(v.valorTotal) " +
            "FROM Venda v GROUP BY v.cliente.nome, v.cliente.cpf")
    List<Object[]> totalPorCliente();

    @Query("SELECT v FROM Venda v WHERE v.dataVencimento > :hoje AND v.statusPagamento = 'PENDENTE'")
    List<Venda> valoresAReceber(LocalDate hoje);

    @Query("SELECT v FROM Venda v WHERE v.dataVencimento < :hoje AND v.statusPagamento = 'PENDENTE'")
    List<Venda> inadimplentes(LocalDate hoje);



    @Query("SELECT v.cliente.nome, v.cliente.cpf, SUM(v.valorTotal) " +
            "FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim " +
            "GROUP BY v.cliente.nome, v.cliente.cpf")
    List<Object[]> totalPorClienteNoPeriodo(LocalDate inicio, LocalDate fim);

    // 🔹 Métodos de busca por tipo e status
    Page<Venda> findByStatusPagamento(StatusPagamento status, Pageable pageable);

    Page<Venda> findByTipoPagamento(String tipoPagamento, Pageable pageable);

    Page<Venda> findByTipoPagamentoAndStatusPagamento(String tipoPagamento, StatusPagamento status, Pageable pageable);
    @Query("SELECT v.tipoPagamento, SUM(v.valorTotal) " +
            "FROM Venda v WHERE v.tipoPagamento IS NOT NULL " +
            "GROUP BY v.tipoPagamento")
    List<Object[]> totalPorFormaPagamento();

    @Query("SELECT v.tipoPagamento, SUM(v.valorTotal) " +
            "FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim " +
            "AND v.tipoPagamento IS NOT NULL " +
            "GROUP BY v.tipoPagamento")
    List<Object[]> totalPorFormaPagamentoNoPeriodo(LocalDate inicio, LocalDate fim);


}
