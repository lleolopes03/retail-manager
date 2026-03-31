package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.FornecedorRequestDto;
import com.br.retailmanager.dtos.FornecedorResponseDto;
import com.br.retailmanager.entity.Fornecedor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {
    Fornecedor toEntity(FornecedorRequestDto dto);
    FornecedorResponseDto toDTO(Fornecedor fornecedor);
    List<FornecedorResponseDto>toResponseDtoList(List<Fornecedor>fornecedors);
}
