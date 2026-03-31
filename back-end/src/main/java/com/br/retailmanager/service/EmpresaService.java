package com.br.retailmanager.service;

import com.br.retailmanager.dtos.EmpresaRequestDto;
import com.br.retailmanager.dtos.EmpresaResponseDto;
import com.br.retailmanager.dtos.mapper.EmpresaMapper;
import com.br.retailmanager.entity.Empresa;
import com.br.retailmanager.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper mapper) {
        this.empresaRepository = empresaRepository;
        this.mapper = mapper;
    }

    public EmpresaResponseDto salvar(EmpresaRequestDto dto) {
        if (empresaRepository.count() > 0) {
            throw new RuntimeException("Já existe uma empresa cadastrada no sistema");
        }
        Empresa empresa = mapper.toEntity(dto);
        Empresa empresaSalvo = empresaRepository.save(empresa);
        return mapper.toDTO(empresaSalvo);
    }
    public EmpresaResponseDto atualizar(Long id, EmpresaRequestDto dto) {
        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        mapper.updateEntityFromDto(dto, empresaExistente); // MapStruct cuida dos sets
        Empresa empresaAtualizada = empresaRepository.save(empresaExistente);

        return mapper.toDTO(empresaAtualizada);
    }
    public EmpresaResponseDto buscarEmpresa() {
        return empresaRepository.findAll()
                .stream()
                .findFirst()
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Nenhuma empresa cadastrada"));
    }

}
