package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.FornecedorRequestDto;
import com.br.isabelaModas.dtos.FornecedorResponseDto;
import com.br.isabelaModas.entity.Fornecedor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {
    Fornecedor toEntity(FornecedorRequestDto dto);
    FornecedorResponseDto toDTO(Fornecedor fornecedor);
    List<FornecedorResponseDto>toResponseDtoList(List<Fornecedor>fornecedors);
}
