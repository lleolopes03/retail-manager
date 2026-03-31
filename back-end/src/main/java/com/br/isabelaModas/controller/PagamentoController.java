package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.PagamentoRequest;
import com.br.isabelaModas.repository.VendaRepository;
import com.br.isabelaModas.service.MercadoPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final MercadoPagoService mercadoPagoService;
    private final VendaRepository vendaRepository;

    public PagamentoController(MercadoPagoService mercadoPagoService, VendaRepository vendaRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.vendaRepository = vendaRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/criar")
    public ResponseEntity<Map<String, String>> criarPagamento(@RequestBody PagamentoRequest req) {
        try {
            String linkPagamento;

            switch (req.getTipo().toUpperCase()) {
                case "DEBITO" -> linkPagamento = mercadoPagoService.criarPagamentoDebito(
                        req.getVendaId(), req.getTitulo(), req.getValorTotal());

                case "CREDITO" -> linkPagamento = mercadoPagoService.criarPagamentoCredito(
                        req.getVendaId(), req.getTitulo(), req.getValorTotal(), req.getNumeroParcelas());

                case "PIX" -> linkPagamento = mercadoPagoService.criarPagamentoPix(
                        req.getVendaId(), req.getTitulo(), req.getValorTotal());

                case "DINHEIRO" -> {
                    // 🔹 Marca a venda como paga em dinheiro
                    vendaRepository.findById(req.getVendaId()).ifPresent(venda -> {
                        venda.setStatusPagamento(com.br.isabelaModas.entity.enums.StatusPagamento.PAGO);
                        venda.setTipoPagamento("DINHEIRO");
                        vendaRepository.save(venda);
                    });
                    // Não há link de pagamento para dinheiro
                    linkPagamento = null;
                }

                default -> throw new IllegalArgumentException("Tipo de pagamento inválido: " + req.getTipo());
            }

            // 🔹 Se for dinheiro, retorna apenas confirmação sem link
            if ("DINHEIRO".equalsIgnoreCase(req.getTipo())) {
                return ResponseEntity.ok(Map.of(
                        "mensagem", "Pagamento registrado em dinheiro",
                        "statusPagamento", "PAGO",
                        "tipoPagamento", "DINHEIRO"
                ));
            }

            return ResponseEntity.ok(Map.of("linkPagamento", linkPagamento));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro inesperado ao criar pagamento: " + e.getMessage()));
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, String>> consultarStatus(@PathVariable("id") Long vendaId) {
        return vendaRepository.findById(vendaId)
                .map(venda -> ResponseEntity.ok(Map.of("statusPagamento", venda.getStatusPagamento().name())))
                .orElse(ResponseEntity.notFound().build());
    }
}
