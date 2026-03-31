package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.ProdutoRequestDto;
import com.br.isabelaModas.dtos.ProdutoResponseDto;
import com.br.isabelaModas.entity.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "categoria", ignore = true) // ✅ ignora categoria
    Produto toEntity(ProdutoRequestDto dto);

    ProdutoResponseDto toDto(Produto entity);

    @Mapping(target = "categoria", ignore = true) // ✅ ignora categoria
    void updateEntityFromDto(ProdutoRequestDto dto, @MappingTarget Produto entity);
}
