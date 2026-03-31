package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.CompraRequestDto;
import com.br.isabelaModas.dtos.CompraResponseDto;
import com.br.isabelaModas.entity.Compra;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", uses = {FornecedorMapper.class})
public interface CompraMapper {
    Compra toEntity(CompraRequestDto dto);
    CompraResponseDto toResponseDto(Compra compra);
    List<CompraResponseDto> toResponseDtoList(List<Compra> compras);


}
