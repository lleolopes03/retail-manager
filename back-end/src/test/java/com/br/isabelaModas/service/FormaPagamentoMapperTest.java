package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.FormaPagamentoRequestDto;
import com.br.isabelaModas.dtos.FormaPagamentoResponseDto;
import com.br.isabelaModas.dtos.mapper.FormaPagamentoMapper;
import com.br.isabelaModas.entity.FormaPagamento;
import com.br.isabelaModas.entity.enums.TipoPagamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.br.isabelaModas.TestSecurityConfig.class)
public class FormaPagamentoMapperTest {
    @Autowired
    private FormaPagamentoMapper mapper;

    @Test
    void deveConverterRequestParaEntity() {
        FormaPagamentoRequestDto dto = new FormaPagamentoRequestDto();
        dto.setTipo(TipoPagamento.DEBITO);
        dto.setValorTotal(BigDecimal.valueOf(100));

        FormaPagamento entity = mapper.toEntity(dto);

        assertEquals(TipoPagamento.DEBITO, entity.getTipo());
        assertEquals(BigDecimal.valueOf(100), entity.getValorTotal());
    }

    @Test
    void deveConverterEntityParaResponse() {
        FormaPagamento entity = new FormaPagamento();
        entity.setId(1L);
        entity.setTipo(TipoPagamento.DINHEIRO);
        entity.setValorTotal(BigDecimal.valueOf(50));

        FormaPagamentoResponseDto response = mapper.toResponseDto(entity);

        assertEquals(1L, response.getId());
        assertEquals(TipoPagamento.DINHEIRO, response.getTipo());
    }


}
