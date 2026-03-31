package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.MovimentacaoEstoqueRequestDto;
import com.br.isabelaModas.dtos.MovimentacaoEstoqueResponseDto;
import com.br.isabelaModas.entity.MovimentacaoEstoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovimentacaoEstoqueMapper {
    // Converte DTO de requisição para entidade (sem criar Produto/Cliente/Venda)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "venda", ignore = true)
    MovimentacaoEstoque toEntity(MovimentacaoEstoqueRequestDto dto);

    // Converte entidade para DTO de resposta
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "vendaId", source = "venda.id")
    @Mapping(target = "clienteId", source = "cliente.id")
    MovimentacaoEstoqueResponseDto toResponseDto(MovimentacaoEstoque movimentacao);

    java.util.List<MovimentacaoEstoqueResponseDto> toResponseDtoList(java.util.List<MovimentacaoEstoque> movimentacoes);

    void updateEntityFromDto(MovimentacaoEstoqueRequestDto dto, @MappingTarget MovimentacaoEstoque movimentacao);







}
