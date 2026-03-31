package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.CompraRequestDto;
import com.br.retailmanager.dtos.CompraResponseDto;
import com.br.retailmanager.entity.Compra;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", uses = {FornecedorMapper.class})
public interface CompraMapper {
    Compra toEntity(CompraRequestDto dto);
    CompraResponseDto toResponseDto(Compra compra);
    List<CompraResponseDto> toResponseDtoList(List<Compra> compras);


}
