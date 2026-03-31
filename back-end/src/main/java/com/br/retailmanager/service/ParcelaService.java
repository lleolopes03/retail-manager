package com.br.retailmanager.service;

import com.br.retailmanager.dtos.RelatorioInadimplenciaGeralDto;
import com.br.retailmanager.entity.Parcela;
import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.entity.enums.StatusPagamento;
import com.br.retailmanager.repository.ParcelaRepository;
import com.br.retailmanager.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;
    private final VendaRepository vendaRepository;

    public ParcelaService(ParcelaRepository parcelaRepository, VendaRepository vendaRepository) {
        this.parcelaRepository = parcelaRepository;
        this.vendaRepository = vendaRepository;
    }

    // Atualiza status de uma parcela
    public void atualizarStatus(Long parcelaId, StatusPagamento status) {
        parcelaRepository.findById(parcelaId).ifPresent(parcela -> {
            parcela.setStatus(status);
            parcelaRepository.save(parcela);

            // 🔹 Verifica se todas as parcelas da venda estão pagas
            Long vendaId = parcela.getVenda().getId();
            List<Parcela> parcelas = parcelaRepository.findByVendaId(vendaId);

            boolean todasPagas = parcelas.stream()
                    .allMatch(p -> p.getStatus() == StatusPagamento.PAGO);

            if (todasPagas) {
                // Atualiza status da venda para PAGO
                vendaRepository.findById(vendaId).ifPresent(venda -> {
                    venda.setStatusPagamento(StatusPagamento.PAGO);
                    vendaRepository.save(venda);
                });
            }
        });
    }

    // Busca parcelas de uma venda
    public List<Parcela> buscarPorVenda(Long vendaId) {
        return parcelaRepository.findByVendaId(vendaId);
    }

    // Gera parcelas automaticamente ao cadastrar uma venda parcelada
    @Transactional
    public void gerarParcelas(Venda venda, int numeroParcelas, LocalDate primeiraParcela, int intervaloDias) {
        // 🔹 Apaga parcelas antigas para evitar duplicação
        parcelaRepository.deleteByVendaId(venda.getId());

        BigDecimal valorParcela = venda.getValorTotal()
                .divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= numeroParcelas; i++) {
            Parcela parcela = new Parcela();
            parcela.setNumero(i);
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(primeiraParcela.plusDays((long) intervaloDias * (i - 1)));
            parcela.setStatus(StatusPagamento.PENDENTE);
            parcela.setVenda(venda);

            parcelaRepository.save(parcela);
        }
    }
    public List<Parcela> buscarParcelasAtrasadas() {
        LocalDate hoje = LocalDate.now();
        return parcelaRepository.findByStatusAndDataVencimentoBefore(StatusPagamento.PENDENTE, hoje);
    }
    public Optional<Parcela> buscarPorId(Long parcelaId) {
        return parcelaRepository.findById(parcelaId);
    }
    public List<Parcela> buscarParcelasPendentes() {
        return parcelaRepository.findByStatus(StatusPagamento.PENDENTE);
    }
    public List<RelatorioInadimplenciaGeralDto> buscarInadimplenciaGeral() {
        LocalDate hoje = LocalDate.now();
        List<RelatorioInadimplenciaGeralDto> resultado = new ArrayList<>();

        // 🔹 1. Buscar vendas vencidas (não carnê)
        List<Venda> vendasVencidas = vendaRepository.inadimplentes(hoje);
        for (Venda v : vendasVencidas) {
            // Só incluir se NÃO for carnê (para evitar duplicação)
            if (!"CARNE".equalsIgnoreCase(v.getTipoPagamento())) {
                resultado.add(new RelatorioInadimplenciaGeralDto(
                        v.getCliente().getNome(),
                        v.getCliente().getCpf(),
                        "VENDA",
                        "Venda #" + v.getId(),
                        v.getValorTotal(),
                        v.getDataVencimento(),
                        ChronoUnit.DAYS.between(v.getDataVencimento(), hoje)
                ));
            }
        }

        // 🔹 2. Buscar parcelas atrasadas
        List<Parcela> parcelasAtrasadas = parcelaRepository.findByStatusAndDataVencimentoBefore(
                StatusPagamento.PENDENTE, hoje
        );
        for (Parcela p : parcelasAtrasadas) {
            if (p.getVenda() != null && p.getVenda().getCliente() != null) {
                resultado.add(new RelatorioInadimplenciaGeralDto(
                        p.getVenda().getCliente().getNome(),
                        p.getVenda().getCliente().getCpf(),
                        "PARCELA",
                        "Parcela " + p.getNumero() + " - Venda #" + p.getVenda().getId(),
                        p.getValor(),
                        p.getDataVencimento(),
                        ChronoUnit.DAYS.between(p.getDataVencimento(), hoje)
                ));
            }
        }

        // 🔹 Ordenar por dias de atraso (decrescente)
        resultado.sort((a, b) -> Long.compare(b.diasAtraso(), a.diasAtraso()));

        return resultado;
    }




}