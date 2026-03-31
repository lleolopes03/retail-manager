package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.EmpresaRequestDto;
import com.br.isabelaModas.dtos.EmpresaResponseDto;
import com.br.isabelaModas.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    Empresa toEntity(EmpresaRequestDto dto);

    EmpresaResponseDto toDTO(Empresa entity);
    void updateEntityFromDto(EmpresaRequestDto dto, @MappingTarget Empresa entity);
}



