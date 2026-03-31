package com.br.retailmanager.repository;

import com.br.retailmanager.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque,Long> {

    // Busca todas movimentações de um cliente pelo CPF
    List<MovimentacaoEstoque> findByClienteCpf(String cpf);

    // Busca todas movimentações de um cliente pelo nome
    List<MovimentacaoEstoque> findByClienteNomeContainingIgnoreCase(String nome);

    // Busca movimentações de um cliente em um intervalo de datas
    List<MovimentacaoEstoque> findByClienteCpfAndDataMovimentacaoBetween(
            String cpf, LocalDateTime inicio, LocalDateTime fim);

    // Busca movimentações por produto
    List<MovimentacaoEstoque> findByProdutoId(Long produtoId);


}
