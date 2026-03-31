package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.ClienteRequestDto;
import com.br.retailmanager.dtos.ClienteResponseDto;
import com.br.retailmanager.dtos.EnderecoDto;
import com.br.retailmanager.entity.Cliente;
import com.br.retailmanager.entity.Endereco;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "idade", expression = "java(calcularIdade(entity.getDataNascimento()))")
    @Mapping(target = "aniversarioHoje", expression = "java(verificarAniversario(entity.getDataNascimento()))")
    ClienteResponseDto toResponseDto(Cliente entity);

    Cliente toEntity(ClienteRequestDto dto);

    List<ClienteResponseDto> toResponseDtoList(List<Cliente> clientes);

    // Conversão de EnderecoDto ↔ Endereco
    default Endereco toEndereco(EnderecoDto dto) {
        if (dto == null) return null;
        Endereco endereco = new Endereco();
        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setLocalidade(dto.getLocalidade()); // ajuste
        endereco.setUf(dto.getUf());                 // ajuste
        return endereco;
    }

    default EnderecoDto toEnderecoDto(Endereco endereco) {
        if (endereco == null) return null;
        EnderecoDto dto = new EnderecoDto();
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade()); // ajuste
        dto.setUf(endereco.getUf());                 // ajuste
        return dto;
    }

    // Cálculo de idade
    default int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    // Verificação de aniversário
    default boolean verificarAniversario(LocalDate dataNascimento) {
        if (dataNascimento == null) return false;
        LocalDate hoje = LocalDate.now();
        return hoje.getMonth() == dataNascimento.getMonth() &&
                hoje.getDayOfMonth() == dataNascimento.getDayOfMonth();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClienteRequestDto dto, @MappingTarget Cliente entity);


}
