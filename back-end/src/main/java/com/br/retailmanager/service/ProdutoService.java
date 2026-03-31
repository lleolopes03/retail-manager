package com.br.retailmanager.service;

import com.br.retailmanager.dtos.ProdutoRequestDto;
import com.br.retailmanager.dtos.ProdutoResponseDto;
import com.br.retailmanager.dtos.mapper.ProdutoMapper;
import com.br.retailmanager.entity.Categoria;
import com.br.retailmanager.entity.Produto;
import com.br.retailmanager.repository.CategoriaRepository;
import com.br.retailmanager.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper mapper;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          ProdutoMapper mapper) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    public ProdutoResponseDto criar(ProdutoRequestDto dto) {
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("O campo categoriaId não pode ser nulo");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Produto produto = mapper.toEntity(dto);
        produto.setCategoria(categoria);

        return mapper.toDto(produtoRepository.save(produto));
    }

    public List<ProdutoResponseDto> listar() {
        return produtoRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }
    public ProdutoResponseDto buscarPorNome(String nome) {
        Produto produto = produtoRepository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return mapper.toDto(produto);
    }

    public List<ProdutoResponseDto> buscarPorNomeParcial(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponseDto> buscarPorTamanho(String tamanho) {
        return produtoRepository.findByTamanhoIgnoreCase(tamanho)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }



    public ProdutoResponseDto atualizar(Long id, ProdutoRequestDto dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        mapper.updateEntityFromDto(dto, produto);

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }

        return mapper.toDto(produtoRepository.save(produto));
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }
    public Page<ProdutoResponseDto> buscarPorNomeOuCor(String termo, int page) {
        Page<Produto> produtos = produtoRepository
                .findByNomeContainingIgnoreCaseOrCorContainingIgnoreCase(
                        termo, termo, PageRequest.of(page, 8)
                );
        return produtos.map(mapper::toDto);
    }

    public Page<ProdutoResponseDto> listarPaginado(int page) {
        Page<Produto> produtos = produtoRepository.findAll(PageRequest.of(page, 8));
        return produtos.map(mapper::toDto);
    }



}
