package com.br.isabelaModas.dtos.mapper;

import com.br.isabelaModas.dtos.VendaRequestDto;
import com.br.isabelaModas.dtos.VendaResponseDto;
import com.br.isabelaModas.entity.Venda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ClienteMapper.class,
                ItemVendaMapper.class,
                StatusPagamentoMapper.class,
                FormaPagamentoMapper.class   // 🔹 incluir aqui
        }
)
public interface VendaMapper {

    Venda toEntity(VendaRequestDto dto);

    @Mapping(source = "tipoPagamento", target = "tipoPagamento")
    @Mapping(source = "numeroParcelas", target = "numeroParcelas")
    @Mapping(source = "formaPagamento", target = "formaPagamento") // 🔹 garante que o objeto venha
    VendaResponseDto toResponseDto(Venda venda);

    List<VendaResponseDto> toResponseDtoList(List<Venda> vendas);
}