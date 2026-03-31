package com.br.retailmanager.controller;

import com.br.retailmanager.dtos.mapper.StatusPagamentoMapper;
import com.br.retailmanager.entity.enums.StatusPagamento;
import com.br.retailmanager.repository.VendaRepository;
import com.br.retailmanager.service.MercadoPagoService;
import com.br.retailmanager.service.ParcelaService;
import com.br.retailmanager.service.VendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class PagamentoWebhookController {

    private final VendaService vendaService;
    private final ParcelaService parcelaService;
    private final StatusPagamentoMapper statusPagamentoMapper;
    private final MercadoPagoService mercadoPagoService;

    public PagamentoWebhookController(VendaService vendaService,
                                      ParcelaService parcelaService,
                                      StatusPagamentoMapper statusPagamentoMapper,
                                      MercadoPagoService mercadoPagoService) {
        this.vendaService = vendaService;
        this.parcelaService = parcelaService;
        this.statusPagamentoMapper = statusPagamentoMapper;
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/notificacao")
    public ResponseEntity<Void> receberNotificacao(@RequestBody Map<String, Object> payload) {


        try {
            String type = (String) payload.get("type");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String id = data.get("id").toString();

            if ("payment".equalsIgnoreCase(type)) {
                // Consultar detalhes do pagamento
                Map<String, Object> response = mercadoPagoService.consultarPagamento(id);

                String externalReference = (String) response.get("external_reference");
                String status = (String) response.get("status");

                StatusPagamento statusPagamento = statusPagamentoMapper.fromMercadoPago(status);

                // 🔹 Aqui decidimos se é venda ou parcela
                Long refId = Long.valueOf(externalReference);

                if (response.containsKey("metadata") && "parcela".equals(((Map)response.get("metadata")).get("tipo"))) {
                    // Caso tenha metadata indicando que é parcela
                    parcelaService.atualizarStatus(refId, statusPagamento);
                } else {
                    // Caso padrão: atualizar venda
                    vendaService.atualizarStatusPagamento(refId, statusPagamento);
                }
            } else {

            }

        } catch (Exception e) {

        }

        return ResponseEntity.ok().build();
    }
}
