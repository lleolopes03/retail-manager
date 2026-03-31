package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.*;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.repository.VendaRepository;
import com.br.isabelaModas.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vendas")
public class VendaController {
    private final VendaService service;
    private final VendaRepository vendaRepository;

    public VendaController(VendaService service, VendaRepository vendaRepository) {
        this.service = service;
        this.vendaRepository = vendaRepository;
    }

    // Criar venda
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping
    public ResponseEntity<VendaResponseDto> criar(@Valid @RequestBody VendaRequestDto dto) {
        VendaResponseDto response = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Buscar por ID
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Listar todas (sem paginação)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/todas")
    public ResponseEntity<List<VendaResponseDto>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // Listar paginado (máx 8 por página)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping
    public ResponseEntity<Page<VendaResponseDto>> listarPaginado(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.listarPaginado(page));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/buscar/periodo")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorPeriodo(inicio, fim, page));
    }

    // Buscar por nome com paginação
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/buscar/nome")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorNome(@RequestParam String nome,
                                                                @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorNome(nome, page));
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/filtro")
    public ResponseEntity<Page<VendaResponseDto>> buscarComFiltros(
            @RequestParam(required = false) String tipoPagamento,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page) {



        return ResponseEntity.ok(service.buscarComFiltros(tipoPagamento, status, page));
    }

    // Buscar por CPF e intervalo de datas com paginação
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/buscar/cpf-periodo")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorCpfEPeriodo(@RequestParam String cpf,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                                                                       @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorCpfEPeriodo(cpf, inicio, fim, page));
    }

    // Atualizar venda
    @PreAuthorize("hasRole('ADMIN','GERENTE_SISTEMA')")
    @PutMapping("/{id}")
    public ResponseEntity<VendaResponseDto> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody VendaRequestDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // Deletar venda
    @PreAuthorize("hasRole('ADMIN','GERENTE_SISTEMA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Relatório diário
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/diario")
    public ResponseEntity<BigDecimal> relatorioDiario(@RequestParam LocalDate data) {
        return ResponseEntity.ok(service.relatorioDiario(data));
    }

    // Relatório mensal (ou período)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/mensal")
    public ResponseEntity<BigDecimal> relatorioMensal(@RequestParam LocalDate inicio,
                                                      @RequestParam LocalDate fim) {
        return ResponseEntity.ok(service.relatorioMensal(inicio, fim));
    }

    // Relatório por forma de pagamento
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/forma-pagamento")
    public ResponseEntity<List<RelatorioFormaPagamentoDto>> relatorioPorFormaPagamento() {
        return ResponseEntity.ok(service.relatorioPorFormaPagamento());
    }

    // Relatório por cliente
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/clientes")
    public ResponseEntity<List<RelatorioClienteDto>> relatorioPorCliente() {
        return ResponseEntity.ok(service.relatorioPorCliente());
    }

    // Relatório de valores a receber
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/valores-a-receber")
    public ResponseEntity<List<RelatorioValoresAReceberDto>> relatorioValoresAReceber() {
        return ResponseEntity.ok(service.relatorioValoresAReceber());
    }

    // Relatório de inadimplência
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/inadimplencia")
    public ResponseEntity<List<RelatorioInadimplenciaDto>> relatorioInadimplencia() {
        return ResponseEntity.ok(service.relatorioInadimplencia());
    }
    // Buscar por CPF com paginação
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/buscar/cpf")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorCpf(
            @RequestParam String cpf,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorCpf(cpf, page));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/forma-pagamento")
    public List<Map<String, Object>> relatorioFormaPagamento(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        List<Object[]> resultados = vendaRepository.totalPorFormaPagamentoNoPeriodo(inicio, fim);
        return resultados.stream().map(r -> Map.of(
                "tipo", r[0],
                "total", r[1]
        )).toList();
    }


    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/clientes")
    public List<Map<String, Object>> relatorioClientes(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        List<Object[]> resultados = vendaRepository.totalPorClienteNoPeriodo(inicio, fim);
        return resultados.stream().map(r -> Map.of(
                "nomeCliente", r[0],
                "cpf", r[1],
                "totalComprado", r[2]
        )).toList();
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/filtro/forma-pagamento")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorFormaPagamento(
            @RequestParam String tipoPagamento,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorFormaPagamento(tipoPagamento, page));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/filtro/status")
    public ResponseEntity<Page<VendaResponseDto>> buscarPorStatus(
            @RequestParam StatusPagamento status,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.buscarPorStatus(status, page));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/filtro/completo")
    public ResponseEntity<Page<VendaResponseDto>> buscarComFiltroCompleto(
            @RequestParam(required = false) String tipoPagamento,
            @RequestParam(required = false) StatusPagamento status,
            @RequestParam(defaultValue = "0") int page) {

        if (tipoPagamento != null && status != null) {
            return ResponseEntity.ok(service.buscarPorFormaPagamentoEStatus(tipoPagamento, status, page));
        } else if (tipoPagamento != null) {
            return ResponseEntity.ok(service.buscarPorFormaPagamento(tipoPagamento, page));
        } else if (status != null) {
            return ResponseEntity.ok(service.buscarPorStatus(status, page));
        } else {
            return ResponseEntity.ok(service.listarPaginado(page));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PatchMapping("/{vendaId}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long vendaId,
            @RequestParam StatusPagamento novoStatus) {
        service.atualizarStatusPagamento(vendaId, novoStatus);
        return ResponseEntity.ok().build();
    }
}
