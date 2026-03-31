package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.EmpresaRequestDto;
import com.br.retailmanager.dtos.EmpresaResponseDto;
import com.br.retailmanager.dtos.mapper.EmpresaMapper;
import com.br.retailmanager.repository.EmpresaRepository;
import com.br.retailmanager.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/v1/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;


    public EmpresaController(EmpresaService empresaService, EmpresaRepository empresaRepository, EmpresaMapper mapper) {
        this.empresaService = empresaService;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EmpresaResponseDto>criar(@RequestBody @Valid EmpresaRequestDto dto){
        EmpresaResponseDto responseDto=empresaService.salvar(dto);
        URI location=URI.create("api/v1/empresa/"+responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<EmpresaResponseDto> buscar() {
        EmpresaResponseDto responseDto = empresaService.buscarEmpresa();
        return ResponseEntity.ok(responseDto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid EmpresaRequestDto dto) {
        EmpresaResponseDto responseDto = empresaService.atualizar(id, dto);
        return ResponseEntity.ok(responseDto);
    }




}
