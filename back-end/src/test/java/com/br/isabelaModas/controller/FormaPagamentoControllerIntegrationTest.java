package com.br.isabelaModas.controller;

import com.br.isabelaModas.NoSecurityConfig;
import com.br.isabelaModas.TestSecurityConfig;
import com.br.isabelaModas.dtos.FormaPagamentoRequestDto;
import com.br.isabelaModas.dtos.FormaPagamentoResponseDto;
import com.br.isabelaModas.entity.Venda;
import com.br.isabelaModas.entity.enums.TipoPagamento;
import com.br.isabelaModas.repository.VendaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class FormaPagamentoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Disabled("Requer integração real com Mercado Pago — habilitar somente em ambiente de sandbox")
    void deveCriarFormaPagamentoParaVenda() throws Exception {
        Venda venda = vendaRepository.save(new Venda());

        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.PIX);
        dto.setValorTotal(BigDecimal.valueOf(150));

        MvcResult result = mockMvc.perform(post("/api/v1/formas-pagamento/venda/" + venda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("PIX"))
                .andExpect(jsonPath("$.valorTotal").value(150))
                .andReturn();

        // valida se retornou id
        FormaPagamentoResponseDto response =
                objectMapper.readValue(result.getResponse().getContentAsString(), FormaPagamentoResponseDto.class);

        assertThat(response.getId()).isNotNull();
    }

    @Test
    void deveListarFormasPagamento() throws Exception {
        mockMvc.perform(get("/api/v1/formas-pagamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveBuscarFormaPagamentoPorId() throws Exception {
        Venda venda = vendaRepository.save(new Venda());

        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.DINHEIRO);
        dto.setValorTotal(BigDecimal.valueOf(80));

        MvcResult result = mockMvc.perform(post("/api/v1/formas-pagamento/venda/" + venda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        FormaPagamentoResponseDto response =
                objectMapper.readValue(result.getResponse().getContentAsString(), FormaPagamentoResponseDto.class);

        mockMvc.perform(get("/api/v1/formas-pagamento/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("DINHEIRO"))
                .andExpect(jsonPath("$.valorTotal").value(80));
    }

    @Test
    @Disabled("Requer setup completo de Venda com todos os campos obrigatórios")
    void deveAtualizarFormaPagamento() throws Exception {
        Venda venda = vendaRepository.save(new Venda());

        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.CREDITO);
        dto.setNumeroParcelas(3);
        dto.setValorTotal(BigDecimal.valueOf(300));

        MvcResult result = mockMvc.perform(post("/api/v1/formas-pagamento/venda/" + venda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        FormaPagamentoResponseDto response =
                objectMapper.readValue(result.getResponse().getContentAsString(), FormaPagamentoResponseDto.class);

        // Atualiza para débito
        FormaPagamentoRequestDto updateDto = new FormaPagamentoRequestDto();
        updateDto.setTipo(TipoPagamento.DEBITO);
        updateDto.setValorTotal(BigDecimal.valueOf(300));

        mockMvc.perform(put("/api/v1/formas-pagamento/" + response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("DEBITO"));
    }

    @Test
    @Disabled("Requer setup completo de Venda com todos os campos obrigatórios")
    void deveDeletarFormaPagamento() throws Exception {
        Venda venda = vendaRepository.save(new Venda());

        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.DEBITO);
        dto.setValorTotal(BigDecimal.valueOf(100));

        MvcResult result = mockMvc.perform(post("/api/v1/formas-pagamento/venda/" + venda.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        FormaPagamentoResponseDto response =
                objectMapper.readValue(result.getResponse().getContentAsString(), FormaPagamentoResponseDto.class);

        mockMvc.perform(delete("/api/v1/formas-pagamento/" + response.getId()))
                .andExpect(status().isNoContent());
    }



}
