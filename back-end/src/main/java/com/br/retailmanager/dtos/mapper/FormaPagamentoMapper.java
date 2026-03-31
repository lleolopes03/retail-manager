package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.FormaPagamentoRequestDto;
import com.br.retailmanager.dtos.FormaPagamentoResponseDto;
import com.br.retailmanager.entity.FormaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FormaPagamentoMapper {

    // Converte DTO de requisição para entidade
    FormaPagamento toEntity(FormaPagamentoRequestDto dto);

    // Converte entidade para DTO de resposta
    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "numeroParcelas", target = "numeroParcelas")
    @Mapping(source = "linkPagamento", target = "linkPagamento")
    FormaPagamentoResponseDto toResponseDto(FormaPagamento formaPagamento);

    // Converte lista de entidades para lista de DTOs
    List<FormaPagamentoResponseDto> toResponseDtoList(List<FormaPagamento> formas);

    // Atualiza entidade existente com dados do DTO
    void updateEntityFromDto(FormaPagamentoRequestDto dto, @MappingTarget FormaPagamento formaPagamento);
}