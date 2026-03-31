package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.CompraRequestDto;
import com.br.isabelaModas.dtos.CompraResponseDto;
import com.br.isabelaModas.dtos.ItemCompraDto;
import com.br.isabelaModas.dtos.mapper.CompraMapper;
import com.br.isabelaModas.entity.Compra;
import com.br.isabelaModas.entity.Fornecedor;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.repository.CompraRepository;
import com.br.isabelaModas.repository.FornecedorRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CompraService {
    private final CompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CompraMapper compraMapper;
    private final ProdutoRepository produtoRepository;

    public CompraService(CompraRepository compraRepository, FornecedorRepository fornecedorRepository, CompraMapper compraMapper, ProdutoRepository produtoRepository) {
        this.compraRepository = compraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.compraMapper = compraMapper;
        this.produtoRepository = produtoRepository;
    }

    // Criar compra
    @Transactional
    public CompraResponseDto criar(CompraRequestDto dto) {
        // valida fornecedor
        Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        Compra compra = compraMapper.toEntity(dto);
        compra.setFornecedor(fornecedor);

        // calcula valor total
        BigDecimal valorTotal = dto.getItens().stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        compra.setValorTotal(valorTotal);

        // 🔹 Atualiza estoque de cada produto (somando)
        for (ItemCompraDto itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            int novaQuantidade = produto.getEstoqueAtual() + itemDto.getQuantidade();
            produto.setEstoqueAtual(novaQuantidade);

            produtoRepository.save(produto);
        }

        // salva compra
        Compra salva = compraRepository.save(compra);

        return compraMapper.toResponseDto(salva);
    }

    // Buscar por ID
    public CompraResponseDto buscarPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));
        return compraMapper.toResponseDto(compra);
    }

    // Listar todas
    public List<CompraResponseDto> listarTodas() {
        return compraMapper.toResponseDtoList(compraRepository.findAll());
    }

    // Atualizar compra
    public CompraResponseDto atualizar(Long id, CompraRequestDto dto) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

        Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        compra.setDataCompra(dto.getDataCompra());
        compra.setFornecedor(fornecedor);

        // recalcula valor total
        BigDecimal valorTotal = dto.getItens().stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        compra.setValorTotal(valorTotal);

        Compra atualizada = compraRepository.save(compra);
        return compraMapper.toResponseDto(atualizada);
    }

    // Deletar compra
    public void deletar(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));
        compraRepository.delete(compra);
    }


}
