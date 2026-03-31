package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.MovimentacaoEstoqueRequestDto;
import com.br.retailmanager.dtos.MovimentacaoEstoqueResponseDto;
import com.br.retailmanager.dtos.ProdutoEstoqueDto;
import com.br.retailmanager.service.MovimentacaoEstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movimentacoes-estoque")
public class MovimentacaoEstoqueController {
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    // 📌 Registrar saída temporária (experimentação)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/saida-temporaria")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> registrarSaidaTemporaria(
            @RequestBody MovimentacaoEstoqueRequestDto dto) {
        return ResponseEntity.ok(movimentacaoEstoqueService.registrarSaidaTemporaria(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/estoque")
    public ResponseEntity<List<ProdutoEstoqueDto>> relatorioEstoqueAtual() {
        return ResponseEntity.ok(movimentacaoEstoqueService.relatorioEstoqueAtual());
    }

    // 📌 Devolver saída temporária ao estoque
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{id}/devolver")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> devolverAoEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.devolverAoEstoque(id));
    }



    // 📌 Registrar venda definitiva
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/venda")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> registrarVenda(
            @RequestBody MovimentacaoEstoqueRequestDto dto) {
        return ResponseEntity.ok(movimentacaoEstoqueService.registrarVenda(dto));
    }

    // 📌 Registrar devolução
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/devolucao")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> registrarDevolucao(
            @RequestBody MovimentacaoEstoqueRequestDto dto) {
        return ResponseEntity.ok(movimentacaoEstoqueService.registrarDevolucao(dto));
    }

    // 📌 Listar todas movimentações
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<List<MovimentacaoEstoqueResponseDto>> listar() {
        return ResponseEntity.ok(movimentacaoEstoqueService.listar());
    }

    // 📌 Buscar movimentação por ID
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorId(id));
    }

    // 📌 Buscar movimentações por cliente (CPF)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDto>> buscarPorClienteCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorClienteCpf(cpf));
    }

    // 📌 Buscar movimentações por produto
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDto>> buscarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorProduto(produtoId));
    }
    


}
