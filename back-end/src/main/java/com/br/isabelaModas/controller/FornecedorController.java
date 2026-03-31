package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.FornecedorRequestDto;
import com.br.isabelaModas.dtos.FornecedorResponseDto;
import com.br.isabelaModas.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fornecedores")
public class FornecedorController {
    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    // Criar fornecedor
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FornecedorResponseDto> criar(@Valid @RequestBody FornecedorRequestDto dto) {
        FornecedorResponseDto response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Buscar por ID
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Buscar por CNPJ
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<FornecedorResponseDto> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(service.buscarPorCnpj(cnpj));
    }

    // Buscar por Email
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/email/{email}")
    public ResponseEntity<FornecedorResponseDto> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }

    // Buscar por Nome (parcial)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<FornecedorResponseDto>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    // Listar todos
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<FornecedorResponseDto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Atualizar fornecedor
    @PreAuthorize("hasRole('ADMIN','GERENTE_SISTEMA')")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDto> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody FornecedorRequestDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // Deletar fornecedor
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
