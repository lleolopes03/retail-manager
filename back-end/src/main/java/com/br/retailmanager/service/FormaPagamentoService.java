package com.br.retailmanager.service;

import com.br.retailmanager.dtos.FormaPagamentoRequestDto;
import com.br.retailmanager.dtos.FormaPagamentoResponseDto;
import com.br.retailmanager.dtos.mapper.FormaPagamentoMapper;
import com.br.retailmanager.entity.FormaPagamento;
import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.repository.FormaPagamentoRepository;
import com.br.retailmanager.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FormaPagamentoService {
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final FormaPagamentoMapper formaPagamentoMapper;
    private final MercadoPagoService mercadoPagoService;
    private final VendaRepository vendaRepository;

    public FormaPagamentoService(FormaPagamentoRepository formaPagamentoRepository,
                                 FormaPagamentoMapper formaPagamentoMapper,
                                 MercadoPagoService mercadoPagoService,
                                 VendaRepository vendaRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
        this.formaPagamentoMapper = formaPagamentoMapper;
        this.mercadoPagoService = mercadoPagoService;
        this.vendaRepository = vendaRepository;
    }

    // Criar forma de pagamento
    public FormaPagamentoResponseDto criar(FormaPagamentoRequestDto dto, Venda venda) {
        FormaPagamento formaPagamento = formaPagamentoMapper.toEntity(dto);
        formaPagamento.setVenda(venda);

        switch (dto.getTipo()) {
            case DINHEIRO -> formaPagamento.setLinkPagamento(null);

            case DEBITO -> {
                String link = mercadoPagoService.criarPagamentoDebito(
                        dto.getVendaId(),
                        "Compra Isabela Modas - Débito",
                        dto.getValorTotal()
                );
                formaPagamento.setLinkPagamento(link);
            }

            case CREDITO -> {
                int parcelas = dto.getNumeroParcelas() != null ? dto.getNumeroParcelas() : 1;
                if (parcelas > 4) parcelas = 4; // limite de parcelas
                String link = mercadoPagoService.criarPagamentoCredito(
                        dto.getVendaId(),
                        "Compra Isabela Modas - Crédito",
                        dto.getValorTotal(),
                        parcelas
                );
                formaPagamento.setNumeroParcelas(parcelas);
                formaPagamento.setLinkPagamento(link);
            }

            case PIX -> {
                String pixCode = mercadoPagoService.criarPagamentoPix(
                        dto.getVendaId(),
                        "Compra Isabela Modas - Pix",
                        dto.getValorTotal()
                );
                formaPagamento.setLinkPagamento(pixCode);
            }

            case CARNE -> {
                // ❌ não gerar parcelas aqui
                // parcelas já são geradas no VendaService.criar
            }
        }

        FormaPagamento salvo = formaPagamentoRepository.save(formaPagamento);

        // 🔹 associa à venda
        venda.setFormaPagamento(salvo);

        // 🔹 persiste a relação
        vendaRepository.save(venda);

        return formaPagamentoMapper.toResponseDto(salvo);
    }

    // Buscar por ID
    public FormaPagamentoResponseDto buscarPorId(Long id) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));
        return formaPagamentoMapper.toResponseDto(formaPagamento);
    }

    // Listar todas
    public List<FormaPagamentoResponseDto> listar() {
        return formaPagamentoMapper.toResponseDtoList(formaPagamentoRepository.findAll());
    }

    // Atualizar
    public FormaPagamentoResponseDto atualizar(Long id, FormaPagamentoRequestDto dto) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));

        formaPagamentoMapper.updateEntityFromDto(dto, formaPagamento);
        FormaPagamento atualizado = formaPagamentoRepository.save(formaPagamento);
        return formaPagamentoMapper.toResponseDto(atualizado);
    }

    // Deletar
    public void deletar(Long id) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));
        formaPagamentoRepository.delete(formaPagamento);
    }
}