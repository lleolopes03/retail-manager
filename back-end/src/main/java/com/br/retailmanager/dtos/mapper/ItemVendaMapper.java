package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.dtos.ItemVendaDto;
import com.br.retailmanager.entity.ItemVenda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public interface ItemVendaMapper {

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "produtoNome")
    @Mapping(target = "subtotal", expression = "java(entity.getPrecoUnitario().multiply(java.math.BigDecimal.valueOf(entity.getQuantidade())))")
    ItemVendaDto toDto(ItemVenda entity);

    List<ItemVendaDto> toDtoList(List<ItemVenda> entities);

    @Mapping(source = "produtoId", target = "produto.id")
    ItemVenda toEntity(ItemVendaDto dto);

    List<ItemVenda> toEntityList(List<ItemVendaDto> dtos);
}