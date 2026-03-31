package com.br.retailmanager.repository;

import com.br.retailmanager.entity.Parcela;
import com.br.retailmanager.entity.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
    List<Parcela> findByStatusAndDataVencimentoBefore(StatusPagamento status, LocalDate data);
    List<Parcela> findByVendaId(Long vendaId);
    void deleteByVendaId(Long vendaId);
    List<Parcela> findByStatus(StatusPagamento status);

}