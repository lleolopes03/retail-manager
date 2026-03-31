package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.FuncionarioRequestDto;
import com.br.isabelaModas.dtos.FuncionarioResponseDto;
import com.br.isabelaModas.entity.Funcionario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {
    // RequestDto → Entidade
    Funcionario toEntity(FuncionarioRequestDto dto);

    // Entidade → ResponseDto
    FuncionarioResponseDto toResponseDto(Funcionario funcionario);

    // Lista de entidades → Lista de ResponseDto
    List<FuncionarioResponseDto> toResponseDtoList(List<Funcionario> funcionarios);





}
