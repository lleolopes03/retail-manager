package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.EmpresaRequestDto;
import com.br.retailmanager.dtos.EmpresaResponseDto;
import com.br.retailmanager.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    Empresa toEntity(EmpresaRequestDto dto);

    EmpresaResponseDto toDTO(Empresa entity);
    void updateEntityFromDto(EmpresaRequestDto dto, @MappingTarget Empresa entity);
}



