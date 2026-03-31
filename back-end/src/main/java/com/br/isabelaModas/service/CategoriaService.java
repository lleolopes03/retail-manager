package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.CategoriaRequestDto;
import com.br.isabelaModas.dtos.CategoriaResponseDto;
import com.br.isabelaModas.dtos.mapper.CategoriaMapper;
import com.br.isabelaModas.entity.Categoria;
import com.br.isabelaModas.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;


    public CategoriaService(CategoriaRepository repository, CategoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CategoriaResponseDto criar(CategoriaRequestDto dto) {
        Categoria categoria = mapper.toEntity(dto);
        return mapper.toDto(repository.save(categoria));
    }

    public List<CategoriaResponseDto> listar() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoriaResponseDto buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }
    public CategoriaResponseDto buscarPorNome(String nome) {
        Categoria categoria = repository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        return mapper.toDto(categoria);
    }

    public List<CategoriaResponseDto> buscarPorNomeParcial(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }



    public CategoriaResponseDto atualizar(Long id, CategoriaRequestDto dto) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        mapper.updateEntityFromDto(dto, categoria);
        return mapper.toDto(repository.save(categoria));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }


}
