package com.br.isabelaModas.service;

import com.br.isabelaModas.entity.Parcela;
import com.br.isabelaModas.entity.Venda;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.repository.ParcelaRepository;
import com.br.isabelaModas.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelaServiceTest {

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private ParcelaService service;

    private Venda venda;
    private Parcela parcela;

    @BeforeEach
    void setUp() {
        venda = new Venda();
        venda.setId(1L);
        venda.setValorTotal(new BigDecimal("300.00"));

        parcela = new Parcela();
        parcela.setId(1L);
        parcela.setNumero(1);
        parcela.setValor(new BigDecimal("100.00"));
        parcela.setDataVencimento(LocalDate.now().plusDays(30));
        parcela.setStatus(StatusPagamento.PENDENTE);
        parcela.setVenda(venda);
    }

    @Test
    void deveGerarParcelasCorretamente() {
        LocalDate primeiraParcela = LocalDate.of(2025, 1, 10);

        service.gerarParcelas(venda, 3, primeiraParcela, 30);

        ArgumentCaptor<Parcela> captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());

        List<Parcela> geradas = captor.getAllValues();
        assertEquals(3, geradas.size());
        assertEquals(new BigDecimal("100.00"), geradas.get(0).getValor());
        assertEquals(new BigDecimal("100.00"), geradas.get(1).getValor());
        assertEquals(new BigDecimal("100.00"), geradas.get(2).getValor());
        assertEquals(StatusPagamento.PENDENTE, geradas.get(0).getStatus());
    }

    @Test
    void deveGerarParcelasComDatasCorretas() {
        LocalDate primeiraParcela = LocalDate.of(2025, 1, 10);

        service.gerarParcelas(venda, 3, primeiraParcela, 30);

        ArgumentCaptor<Parcela> captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());

        List<Parcela> geradas = captor.getAllValues();
        assertEquals(LocalDate.of(2025, 1, 10), geradas.get(0).getDataVencimento());
        assertEquals(LocalDate.of(2025, 2, 9), geradas.get(1).getDataVencimento());
        assertEquals(LocalDate.of(2025, 3, 11), geradas.get(2).getDataVencimento());
    }

    @Test
    void deveNumerarParcelasSequencialmente() {
        service.gerarParcelas(venda, 3, LocalDate.now(), 30);

        ArgumentCaptor<Parcela> captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());

        List<Parcela> geradas = captor.getAllValues();
        assertEquals(1, geradas.get(0).getNumero());
        assertEquals(2, geradas.get(1).getNumero());
        assertEquals(3, geradas.get(2).getNumero());
    }

    @Test
    void deveApagarParcelasAntigasAoGerarNovas() {
        service.gerarParcelas(venda, 3, LocalDate.now(), 30);

        verify(parcelaRepository).deleteByVendaId(venda.getId());
    }

    @Test
    void deveAtualizarStatusParcelaParaPago() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(parcelaRepository.findByVendaId(1L)).thenReturn(List.of(parcela));
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);

        service.atualizarStatus(1L, StatusPagamento.PAGO);

        assertEquals(StatusPagamento.PAGO, parcela.getStatus());
        verify(parcelaRepository).save(parcela);
    }

    @Test
    void deveAtualizarVendaParaPagoQuandoTodasParcelasPagas() {
        parcela.setStatus(StatusPagamento.PAGO);
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(parcelaRepository.findByVendaId(1L)).thenReturn(List.of(parcela));
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);

        service.atualizarStatus(1L, StatusPagamento.PAGO);

        assertEquals(StatusPagamento.PAGO, venda.getStatusPagamento());
        verify(vendaRepository).save(venda);
    }

    @Test
    void naoDeveAtualizarVendaSeAindaHaParcelasPendentes() {
        Parcela parcelaPendente = new Parcela();
        parcelaPendente.setId(2L);
        parcelaPendente.setStatus(StatusPagamento.PENDENTE);
        parcelaPendente.setVenda(venda);

        parcela.setStatus(StatusPagamento.PAGO);

        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(parcelaRepository.findByVendaId(1L)).thenReturn(List.of(parcela, parcelaPendente));

        service.atualizarStatus(1L, StatusPagamento.PAGO);

        verify(vendaRepository, never()).save(any());
    }

    @Test
    void deveBuscarParcelasDeUmaVenda() {
        when(parcelaRepository.findByVendaId(1L)).thenReturn(List.of(parcela));

        List<Parcela> resultado = service.buscarPorVenda(1L);

        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("100.00"), resultado.get(0).getValor());
    }

    @Test
    void deveBuscarParcelasAtrasadas() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        parcela.setDataVencimento(ontem);

        when(parcelaRepository.findByStatusAndDataVencimentoBefore(eq(StatusPagamento.PENDENTE), any(LocalDate.class)))
                .thenReturn(List.of(parcela));

        List<Parcela> resultado = service.buscarParcelasAtrasadas();

        assertEquals(1, resultado.size());
        assertEquals(StatusPagamento.PENDENTE, resultado.get(0).getStatus());
    }

    @Test
    void deveBuscarParcelasPendentes() {
        when(parcelaRepository.findByStatus(StatusPagamento.PENDENTE)).thenReturn(List.of(parcela));

        List<Parcela> resultado = service.buscarParcelasPendentes();

        assertEquals(1, resultado.size());
        verify(parcelaRepository).findByStatus(StatusPagamento.PENDENTE);
    }

    @Test
    void deveBuscarParcelaPorId() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));

        Optional<Parcela> resultado = service.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(new BigDecimal("100.00"), resultado.get().getValor());
    }

    @Test
    void deveRetornarVazioParaParcelaInexistente() {
        when(parcelaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Parcela> resultado = service.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }
}
