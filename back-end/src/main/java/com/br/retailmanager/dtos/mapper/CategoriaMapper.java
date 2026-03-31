package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.CategoriaRequestDto;
import com.br.retailmanager.dtos.CategoriaResponseDto;
import com.br.retailmanager.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

     // ✅ não mapeia lista de produtos
    CategoriaResponseDto toDto(Categoria entity);

    @Mapping(target = "produtos", ignore = true)
    Categoria toEntity(CategoriaRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    void updateEntityFromDto(CategoriaRequestDto dto, @MappingTarget Categoria entity);
}