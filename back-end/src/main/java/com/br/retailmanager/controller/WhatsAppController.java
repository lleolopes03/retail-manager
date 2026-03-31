package com.br.retailmanager.controller;

import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.service.WhatsAppService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class WhatsAppController {

    private final WhatsAppService whatsappService;

    public WhatsAppController(WhatsAppService whatsappService) {
        this.whatsappService = whatsappService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/boas-vindas")
    public void enviarBoasVindas(@RequestParam String numero, @RequestParam String nome) {
        whatsappService.enviarBoasVindas(numero, nome);
    }


    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/venda")
    public void enviarResumoVenda(@RequestParam String numero, @RequestBody Venda venda, @RequestParam(required = false) String linkPagamento) {
        whatsappService.enviarResumoVenda(numero, venda, linkPagamento);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/recibo")
    public void enviarRecibo(@RequestParam String numero, @RequestParam String pdfUrl) {
        whatsappService.enviarReciboPdf(numero, pdfUrl);
    }

}
