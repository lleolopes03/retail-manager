package com.br.retailmanager.dtos.mapper;

import com.br.retailmanager.entity.enums.StatusPagamento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusPagamentoMapper {
    default StatusPagamento fromMercadoPago(String status) {
        if (status == null) return StatusPagamento.PENDENTE;
        return switch (status.toLowerCase()) {
            case "approved" -> StatusPagamento.PAGO;
            case "pending" -> StatusPagamento.PENDENTE;
            case "rejected" -> StatusPagamento.REJEITADO;
            case "in_process" -> StatusPagamento.EM_PROCESSO;
            case "cancelled" -> StatusPagamento.CANCELADO;
            default -> StatusPagamento.PENDENTE;
        };
    }

    default String toString(StatusPagamento status) {
        return status != null ? status.name() : "PENDENTE";
    }



}
