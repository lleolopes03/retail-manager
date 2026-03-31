package com.br.retailmanager.service;

import com.br.retailmanager.TestSecurityConfig;
import com.br.retailmanager.dtos.FormaPagamentoRequestDto;
import com.br.retailmanager.dtos.FormaPagamentoResponseDto;
import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.entity.enums.TipoPagamento;
import com.br.retailmanager.repository.VendaRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class FormaPagamentoServiceTest {

    @Autowired
    private FormaPagamentoService formaPagamentoService;

    @Autowired
    private VendaRepository vendaRepository;

    @Test
    @Disabled("Requer setup completo de Venda com cliente, itens e valorTotal — integração pendente")
    void deveCriarFormaPagamentoCredito() {
        Venda venda = vendaRepository.save(new Venda());
        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.CREDITO);
        dto.setNumeroParcelas(3);
        dto.setValorTotal(BigDecimal.valueOf(300));

        FormaPagamentoResponseDto response = formaPagamentoService.criar(dto, venda);

        assertNotNull(response.getId());
        assertEquals(TipoPagamento.CREDITO, response.getTipo());
    }

    @Test
    @Disabled("Requer setup completo de Venda com cliente, itens e valorTotal — integração pendente")
    void deveCriarFormaPagamentoCarne() {
        Venda venda = vendaRepository.save(new Venda());
        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.CARNE);
        dto.setNumeroParcelas(5);
        dto.setPrimeiraParcela(LocalDate.now().plusDays(10));
        dto.setIntervaloDias(30);
        dto.setValorTotal(BigDecimal.valueOf(500));

        FormaPagamentoResponseDto response = formaPagamentoService.criar(dto, venda);

        assertEquals(TipoPagamento.CARNE, response.getTipo());
        assertEquals(5, response.getNumeroParcelas());
    }

    @Test
    @Disabled("Requer integração real com Mercado Pago — habilitar somente em ambiente de sandbox")
    void deveCriarFormaPagamentoPixAvista() {
        Venda venda = vendaRepository.save(new Venda());
        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValorTotal(BigDecimal.valueOf(200));

        FormaPagamentoResponseDto response = formaPagamentoService.criar(dto, venda);

        assertEquals(TipoPagamento.PIX, response.getTipo());
        assertNotNull(response.getLinkPagamento());
    }
}
