package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.FormaPagamentoRequestDto;
import com.br.retailmanager.dtos.FormaPagamentoResponseDto;
import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.repository.VendaRepository;
import com.br.retailmanager.service.FormaPagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/formas-pagamento")
public class FormaPagamentoController {
    private final FormaPagamentoService formaPagamentoService;
    private final VendaRepository vendaRepository;

    public FormaPagamentoController(FormaPagamentoService formaPagamentoService,
                                    VendaRepository vendaRepository) {
        this.formaPagamentoService = formaPagamentoService;
        this.vendaRepository = vendaRepository;
    }

    // Criar forma de pagamento vinculada a uma venda
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/venda/{vendaId}")
    public ResponseEntity<FormaPagamentoResponseDto> criarFormaPagamento(
            @PathVariable Long vendaId,
            @RequestBody FormaPagamentoRequestDto dto) {

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        FormaPagamentoResponseDto response = formaPagamentoService.criar(dto, venda);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Buscar por ID
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<FormaPagamentoResponseDto> buscarPorId(@PathVariable Long id) {
        FormaPagamentoResponseDto response = formaPagamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Listar todas
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<FormaPagamentoResponseDto>> listar() {
        List<FormaPagamentoResponseDto> formas = formaPagamentoService.listar();
        return ResponseEntity.ok(formas);
    }

    // Atualizar
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FormaPagamentoResponseDto> atualizar(
            @PathVariable Long id,
            @RequestBody FormaPagamentoRequestDto dto) {

        FormaPagamentoResponseDto response = formaPagamentoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    // Deletar
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        formaPagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
