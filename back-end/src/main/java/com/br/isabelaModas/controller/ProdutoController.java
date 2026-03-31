package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.ProdutoRequestDto;
import com.br.isabelaModas.dtos.ProdutoResponseDto;
import com.br.isabelaModas.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProdutoResponseDto> criar(@RequestBody ProdutoRequestDto dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<ProdutoResponseDto> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/search")
    public ResponseEntity<List<ProdutoResponseDto>> buscarPorNomeParcial(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNomeParcial(nome));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/tamanho/{tamanho}")
    public ResponseEntity<List<ProdutoResponseDto>> buscarPorTamanho(@PathVariable String tamanho) {
        return ResponseEntity.ok(service.buscarPorTamanho(tamanho));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> atualizar(@PathVariable Long id,
                                                        @RequestBody ProdutoRequestDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProdutoResponseDto>> listarPaginado(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.listarPaginado(page));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/buscar")
    public ResponseEntity<Page<ProdutoResponseDto>> buscarPorNomeOuCor(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorNomeOuCor(termo, page));
    }



}
