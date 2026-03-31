package com.br.isabelaModas.service;

import com.br.isabelaModas.entity.ItemVenda;
import com.br.isabelaModas.entity.Parcela;
import com.br.isabelaModas.entity.Venda;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.token}")
    private String whatsappToken;

    @Value("${whatsapp.phoneNumberId}")
    private String phoneNumberId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarBoasVindas(String numero, String nomeCliente) {
        String mensagem = "🎉 Bem-vindo(a), " + nomeCliente + "! Agora você faz parte da nossa loja.";
        enviarMensagemTexto(numero, mensagem);
    }

    public void enviarResumoVenda(String numero, Venda venda, String linkPagamento) {
        StringBuilder mensagem = new StringBuilder("🛒 Compra registrada!\nItens:\n");
        for (ItemVenda item : venda.getItens()) {
            mensagem.append("- ")
                    .append(item.getProduto())
                    .append(" (")
                    .append(item.getQuantidade())
                    .append("x) - R$ ")
                    .append(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .append("\n");
        }
        mensagem.append("\nTotal: R$ ").append(venda.getValorTotal());
        if (linkPagamento != null) {
            mensagem.append("\n\n💳 Pague aqui: ").append(linkPagamento);
        }
        enviarMensagemTexto(numero, mensagem.toString());
    }


    public void enviarReciboPdf(String numero, String pdfUrlPublica) {
        String url = whatsappApiUrl + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(whatsappToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{ \"messaging_product\": \"whatsapp\", " +
                "\"to\": \"" + numero + "\", " +
                "\"type\": \"document\", " +
                "\"document\": { \"link\": \"" + pdfUrlPublica + "\", \"caption\": \"Recibo da compra\" } }";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }


    private void enviarMensagemTexto(String numero, String mensagem) {
        String url = whatsappApiUrl + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(whatsappToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{ \"messaging_product\": \"whatsapp\", " +
                "\"to\": \"" + numero + "\", " +
                "\"type\": \"text\", " +
                "\"text\": { \"body\": \"" + mensagem + "\" } }";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
    public void enviarCobrancaParcela(String numero, String nomeCliente, Parcela parcela, String linkPagamento) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("⚠️ Olá, ").append(nomeCliente).append("!\n");
        mensagem.append("Identificamos que sua parcela nº ").append(parcela.getNumero())
                .append(" no valor de R$ ").append(parcela.getValor())
                .append(" venceu em ").append(parcela.getDataVencimento()).append(".\n");

        mensagem.append("Atualmente está em atraso há ")
                .append(ChronoUnit.DAYS.between(parcela.getDataVencimento(), LocalDate.now()))
                .append(" dias.\n");

        mensagem.append("\n💳 Você pode regularizar agora pelo link: ").append(linkPagamento);

        enviarMensagemTexto(numero, mensagem.toString());
    }
}
