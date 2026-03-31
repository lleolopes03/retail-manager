package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.CompraRequestDto;
import com.br.retailmanager.dtos.CompraResponseDto;
import com.br.retailmanager.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {
    private final CompraService service;

    public CompraController(CompraService service) {
        this.service = service;
    }

    // Criar compra
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_SISTEMA')")
    @PostMapping
    public ResponseEntity<CompraResponseDto> criar(@Valid @RequestBody CompraRequestDto dto) {
        CompraResponseDto response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Buscar por ID
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Listar todas
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<CompraResponseDto>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // Atualizar compra
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_SISTEMA')")
    @PutMapping("/{id}")
    public ResponseEntity<CompraResponseDto> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody CompraRequestDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // Deletar compra
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
