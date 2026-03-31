package com.br.retailmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    private final WebClient webClient;

    public MercadoPagoService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.mercadopago.com")
                .build();
    }

    // 🔹 Pagamento débito
    public String criarPagamentoDebito(Long vendaId, String titulo, BigDecimal valorTotal) {
        return criarPreference(vendaId, titulo, valorTotal, 1);
    }

    // 🔹 Pagamento crédito com parcelas fixas
    public String criarPagamentoCredito(Long vendaId, String titulo, BigDecimal valorTotal, int parcelas) {
        if (parcelas > 12) parcelas = 12; // limite de segurança
        return criarPreference(vendaId, titulo, valorTotal, parcelas);
    }

    // 🔹 Pagamento Pix
    public String criarPagamentoPix(Long vendaId, String titulo, BigDecimal valorTotal) {
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", valorTotal.doubleValue());
        body.put("description", titulo);
        body.put("payment_method_id", "pix");
        body.put("external_reference", vendaId.toString());
        body.put("payer", Map.of("email", "comprador@dominio.com"));

        Map<String, Object> response = webClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null || !response.containsKey("point_of_interaction")) {
            throw new IllegalStateException("MercadoPago não retornou PIX. Resposta: " + response);
        }

        Map<String, Object> poi = (Map<String, Object>) response.get("point_of_interaction");
        Map<String, Object> transactionData = (Map<String, Object>) poi.get("transaction_data");

        String ticketUrl = (String) transactionData.get("ticket_url");
        if (ticketUrl == null) {
            throw new IllegalStateException("MercadoPago não retornou ticket_url. Resposta: " + response);
        }

        return ticketUrl;
    }

    // 🔹 Método interno para criar preference (crédito/débito)
    private String criarPreference(Long vendaId, String titulo, BigDecimal valorTotal, int parcelas) {
        Map<String, Object> item = new HashMap<>();
        item.put("title", titulo);
        item.put("quantity", 1);
        item.put("unit_price", valorTotal.doubleValue());
        item.put("currency_id", "BRL");

        Map<String, Object> paymentMethods = new HashMap<>();
        paymentMethods.put("installments", parcelas);          // máximo de parcelas
        paymentMethods.put("default_installments", parcelas);  // 🔹 fixa como padrão

        Map<String, Object> body = new HashMap<>();
        body.put("items", List.of(item));
        body.put("external_reference", vendaId.toString());
        body.put("payer", Map.of("email", "comprador@dominio.com"));
        body.put("payment_methods", paymentMethods);
        body.put("notification_url", webhookUrl);

        Map<String, Object> response = webClient.post()
                .uri("/checkout/preferences")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null || !response.containsKey("init_point")) {
            throw new IllegalStateException("MercadoPago não retornou init_point. Resposta: " + response);
        }

        return (String) response.get("init_point");
    }
    // 🔹 Consultar pagamento pelo ID retornado pelo Mercado Pago
    public Map<String, Object> consultarPagamento(String pagamentoId) {
        try {
            return webClient.get()
                    .uri("/v1/payments/" + pagamentoId)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao consultar pagamento no MercadoPago: " + e.getMessage(), e);
        }
    }
    // 🔹 Gerar link de pagamento para uma parcela específica
    // 🔹 Gerar link de pagamento para uma parcela específica
    public String gerarLinkParcela(Long parcelaId, String titulo, BigDecimal valor) {
        Map<String, Object> item = new HashMap<>();
        item.put("title", titulo);
        item.put("quantity", 1);
        item.put("unit_price", valor.doubleValue());
        item.put("currency_id", "BRL");

        // 🔹 FORÇAR PAGAMENTO À VISTA (1 parcela apenas)
        Map<String, Object> paymentMethods = new HashMap<>();
        paymentMethods.put("installments", 1);          // máximo 1 parcela
        paymentMethods.put("default_installments", 1);  // padrão 1 parcela

        Map<String, Object> body = new HashMap<>();
        body.put("items", List.of(item));
        body.put("external_reference", parcelaId.toString());
        body.put("payer", Map.of("email", "comprador@dominio.com"));
        body.put("payment_methods", paymentMethods); // 🔹 ADICIONAR ESTA LINHA
        body.put("notification_url", webhookUrl);

        Map<String, Object> response = webClient.post()
                .uri("/checkout/preferences")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null || !response.containsKey("init_point")) {
            throw new IllegalStateException("MercadoPago não retornou init_point. Resposta: " + response);
        }

        return (String) response.get("init_point");
    }

}
