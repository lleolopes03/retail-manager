package com.br.retailmanager.service;

import com.br.retailmanager.dtos.FornecedorRequestDto;
import com.br.retailmanager.dtos.FornecedorResponseDto;
import com.br.retailmanager.dtos.mapper.FornecedorMapper;
import com.br.retailmanager.entity.Fornecedor;
import com.br.retailmanager.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {
    private final FornecedorRepository repository;
    private final FornecedorMapper mapper;

    public FornecedorService(FornecedorRepository repository, FornecedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // Criar fornecedor
    public FornecedorResponseDto criar(FornecedorRequestDto dto) {
        // Valida duplicidade
        if (repository.existsByCnpj(dto.getCnpj())) {
            throw new RuntimeException("Já existe fornecedor com este CNPJ");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe fornecedor com este e-mail");
        }

        Fornecedor fornecedor = mapper.toEntity(dto);
        Fornecedor salvo = repository.save(fornecedor);
        return mapper.toDTO(salvo);
    }

    // Buscar por ID
    public FornecedorResponseDto buscarPorId(Long id) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        return mapper.toDTO(fornecedor);
    }

    // Buscar por CNPJ
    public FornecedorResponseDto buscarPorCnpj(String cnpj) {
        Fornecedor fornecedor = repository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        return mapper.toDTO(fornecedor);
    }

    // Buscar por Email
    public FornecedorResponseDto buscarPorEmail(String email) {
        Fornecedor fornecedor = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        return mapper.toDTO(fornecedor);
    }

    // Buscar por Nome (parcial)
    public List<FornecedorResponseDto> buscarPorNome(String nome) {
        return mapper.toResponseDtoList(repository.findByNomeContainingIgnoreCase(nome));
    }

    // Listar todos
    public List<FornecedorResponseDto> listarTodos() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    // Atualizar fornecedor
    public FornecedorResponseDto atualizar(Long id, FornecedorRequestDto dto) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        // Valida duplicidade se CNPJ ou email forem alterados
        if (!fornecedor.getCnpj().equals(dto.getCnpj()) && repository.existsByCnpj(dto.getCnpj())) {
            throw new RuntimeException("Já existe fornecedor com este CNPJ");
        }
        if (!fornecedor.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe fornecedor com este e-mail");
        }

        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setTelefone(dto.getTelefone());
        fornecedor.setEndereco(mapper.toEntity(dto).getEndereco());

        Fornecedor atualizado = repository.save(fornecedor);
        return mapper.toDTO(atualizado);
    }

    // Deletar fornecedor
    public void deletar(Long id) {
        Fornecedor fornecedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        repository.delete(fornecedor);
    }


}
