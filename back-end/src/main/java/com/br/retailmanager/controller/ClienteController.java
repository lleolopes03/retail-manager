package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.ClienteRequestDto;
import com.br.retailmanager.dtos.ClienteResponseDto;
import com.br.retailmanager.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping
    public ResponseEntity<ClienteResponseDto> criar(@Valid @RequestBody ClienteRequestDto dto) {
        ClienteResponseDto cliente = clienteService.criar(dto);
        return ResponseEntity.status(201).body(cliente);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listar() {
        return ResponseEntity.ok(clienteService.listar()); // corrigido para camelCase
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDto> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDto> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(clienteService.buscarPorEmail(email));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<ClienteResponseDto>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(clienteService.buscarPorNomeParcial(nome));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/search")
    public ResponseEntity<List<ClienteResponseDto>> buscarPorNomeParcial(@RequestParam String nome) {
        return ResponseEntity.ok(clienteService.buscarPorNomeParcial(nome));
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE_SISTEMA')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
