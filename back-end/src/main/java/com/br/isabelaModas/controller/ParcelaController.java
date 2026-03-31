package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.ParcelaDto;
import com.br.isabelaModas.dtos.RelatorioInadimplenciaGeralDto;
import com.br.isabelaModas.dtos.RelatorioParcelasAtrasadasDto;
import com.br.isabelaModas.entity.Parcela;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.service.ParcelaService;
import com.br.isabelaModas.service.MercadoPagoService;
import com.br.isabelaModas.service.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/parcelas")
public class ParcelaController {

    private final ParcelaService parcelaService;
    private final MercadoPagoService mercadoPagoService;
    private final WhatsAppService whatsappService;


    public ParcelaController(ParcelaService parcelaService, MercadoPagoService mercadoPagoService, WhatsAppService whatsappService) {
        this.parcelaService = parcelaService;
        this.mercadoPagoService = mercadoPagoService;
        this.whatsappService = whatsappService;
    }

    // 🔹 Listar parcelas de uma venda
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<ParcelaDto>> listarParcelasPorVenda(@PathVariable Long vendaId) {
        List<ParcelaDto> parcelas = parcelaService.buscarPorVenda(vendaId).stream()
                .map(p -> new ParcelaDto( p.getId(),
                        p.getNumero(),
                        p.getValor(),
                        p.getDataVencimento(),
                        p.getStatus(),
                        p.getVenda().getId(),
                        p.getVenda().getCliente().getNome()))
                .toList();
        return ResponseEntity.ok(parcelas);
    }

    // 🔹 Atualizar status de uma parcela (ex.: marcar como PAGO ou ATRASADO)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PutMapping("/{parcelaId}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long parcelaId,
                                                @RequestParam StatusPagamento status) {
        parcelaService.atualizarStatus(parcelaId, status);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Gerar link de pagamento para uma parcela específica
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{parcelaId}/link-pagamento")
    public ResponseEntity<Map<String, String>> gerarLinkPagamento(@PathVariable Long parcelaId) {
        Parcela parcela = parcelaService.buscarPorId(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        String link = mercadoPagoService.gerarLinkParcela(parcela.getId(),
                "Parcela nº " + parcela.getNumero(),
                parcela.getValor());

        return ResponseEntity.ok(Map.of(
                "parcelaId", parcela.getId().toString(),
                "valor", parcela.getValor().toString(),
                "linkPagamento", link
        ));
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/atrasadas")
    public ResponseEntity<List<RelatorioParcelasAtrasadasDto>> relatorioParcelasAtrasadas() {
        LocalDate hoje = LocalDate.now();
        List<Parcela> atrasadas = parcelaService.buscarParcelasAtrasadas();

        // 🔹 FILTRAR parcelas que têm venda e cliente válidos
        List<RelatorioParcelasAtrasadasDto> relatorio = atrasadas.stream()
                .filter(p -> p.getVenda() != null && p.getVenda().getCliente() != null)
                .map(p -> new RelatorioParcelasAtrasadasDto(
                        p.getVenda().getCliente().getNome(),
                        p.getVenda().getCliente().getCpf(),
                        p.getNumero(),
                        p.getValor(),
                        p.getDataVencimento(),
                        ChronoUnit.DAYS.between(p.getDataVencimento(), hoje)
                ))
                .toList();

        return ResponseEntity.ok(relatorio);
    }


    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{parcelaId}/cobranca-whatsapp")
    public ResponseEntity<Void> enviarCobrancaWhatsApp(@PathVariable Long parcelaId,
                                                       @RequestParam String numeroCliente) {
        Parcela parcela = parcelaService.buscarPorId(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        // 🔹 Gera link de pagamento da parcela
        String linkPagamento = mercadoPagoService.gerarLinkParcela(parcela.getId(),
                "Parcela nº " + parcela.getNumero(),
                parcela.getValor());

        // 🔹 Envia mensagem automática de cobrança
        String nomeCliente = parcela.getVenda().getCliente().getNome();
        whatsappService.enviarCobrancaParcela(numeroCliente, nomeCliente, parcela, linkPagamento);

        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/pendentes")
    public ResponseEntity<List<ParcelaDto>> relatorioParcelasPendentes() {
        List<Parcela> pendentes = parcelaService.buscarParcelasPendentes();

        List<ParcelaDto> dto = pendentes.stream()
                .map(p -> new ParcelaDto(
                        p.getId(),
                        p.getNumero(),
                        p.getValor(),
                        p.getDataVencimento(),
                        p.getStatus(),
                        p.getVenda().getId(),
                        p.getVenda().getCliente().getNome()
                ))
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PutMapping("/{parcelaId}/dar-baixa")
    public ResponseEntity<Void> darBaixaManual(@PathVariable Long parcelaId) {
        parcelaService.atualizarStatus(parcelaId, StatusPagamento.PAGO);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/inadimplencia-geral")
    public ResponseEntity<List<RelatorioInadimplenciaGeralDto>> relatorioInadimplenciaGeral() {
        List<RelatorioInadimplenciaGeralDto> relatorio = parcelaService.buscarInadimplenciaGeral();
        return ResponseEntity.ok(relatorio);
    }





}