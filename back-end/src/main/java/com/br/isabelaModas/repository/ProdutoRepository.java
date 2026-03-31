package com.br.isabelaModas.repository;

import com.br.isabelaModas.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto,Long> {
    // Busca exata por nome
    Optional<Produto> findByNome(String nome);

    // Busca parcial por nome (case-insensitive)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca por tamanho
    List<Produto> findByTamanhoIgnoreCase(String tamanho);
    // Relatório de estoque atual
    @Query("SELECT p.nome, p.tamanho, p.estoqueAtual FROM Produto p")
    List<Object[]> relatorioEstoqueAtual();
    Page<Produto> findByNomeContainingIgnoreCaseOrCorContainingIgnoreCase(String nome, String cor, Pageable pageable);





}
