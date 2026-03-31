package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.*;
import com.br.isabelaModas.dtos.mapper.VendaMapper;
import com.br.isabelaModas.entity.*;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.entity.enums.TipoPagamento;
import com.br.isabelaModas.repository.ClienteRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import com.br.isabelaModas.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final VendaMapper vendaMapper;
    private final ParcelaService parcelaService;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository, VendaMapper vendaMapper, ParcelaService parcelaService) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.vendaMapper = vendaMapper;
        this.parcelaService = parcelaService;
    }

    @Transactional
    public VendaResponseDto criar(VendaRequestDto dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Venda venda = vendaMapper.toEntity(dto);
        venda.setCliente(cliente);

        venda.setTipoPagamento(dto.getTipoPagamento());
        venda.setNumeroParcelas(dto.getNumeroParcelas());

        List<ItemVenda> itens = dto.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // 🔹 ADICIONE ESTA VERIFICAÇÃO DE ESTOQUE
            if (produto.getEstoqueAtual() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome() +
                        ". Disponível: " + produto.getEstoqueAtual() + ", Solicitado: " + itemDto.getQuantidade());
            }

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(itemDto.getPrecoUnitario());
            item.setVenda(venda);
            return item;
        }).toList();

        venda.setItens(itens);

        BigDecimal valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        venda.setValorTotal(valorTotal);

        // 🔹 salva a venda
        Venda salva = vendaRepository.save(venda);

        // 🔹 ADICIONE ESTA LÓGICA DE BAIXA NO ESTOQUE
        for (ItemVenda item : salva.getItens()) {
            Produto produto = item.getProduto();
            int novoEstoque = produto.getEstoqueAtual() - item.getQuantidade();
            produto.setEstoqueAtual(novoEstoque);
            produtoRepository.save(produto);
        }

        // se for carnê, gera parcelas
        if ("CARNE".equalsIgnoreCase(dto.getTipoPagamento()) && salva.getNumeroParcelas() > 1) {
            parcelaService.gerarParcelas(
                    salva,
                    salva.getNumeroParcelas(),
                    dto.getPrimeiraParcela(),
                    dto.getIntervaloDias()
            );
        }

        return vendaMapper.toResponseDto(salva);
    }


    // Buscar por ID
    public VendaResponseDto buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        return vendaMapper.toResponseDto(venda);
    }

    // Listar todas (sem paginação)
    public List<VendaResponseDto> listarTodas() {
        return vendaMapper.toResponseDtoList(vendaRepository.findAll());
    }

    // Listar paginado (máx 8 por página)
    public Page<VendaResponseDto> listarPaginado(int page) {
        Page<Venda> vendas = vendaRepository.findAll(PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    // Buscar por CPF com paginação
    public Page<VendaResponseDto> buscarPorCpf(String cpf, int page) {
        Page<Venda> vendas = vendaRepository.findByClienteCpf(cpf, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    // Buscar por nome com paginação
    public Page<VendaResponseDto> buscarPorNome(String nome, int page) {
        Page<Venda> vendas = vendaRepository.findByClienteNomeContainingIgnoreCase(nome, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    // Buscar por CPF e intervalo de datas com paginação
    public Page<VendaResponseDto> buscarPorCpfEPeriodo(String cpf, LocalDate inicio, LocalDate fim, int page) {
        Page<Venda> vendas = vendaRepository.findByClienteCpfAndDataVendaBetween(cpf, inicio, fim, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    // Atualizar venda
    @Transactional
    public VendaResponseDto atualizar(Long id, VendaRequestDto dto) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // 🔹 DEVOLVER O ESTOQUE DOS ITENS ANTIGOS ANTES DE ATUALIZAR
        for (ItemVenda itemAntigo : venda.getItens()) {
            Produto produtoAntigo = itemAntigo.getProduto();
            produtoAntigo.setEstoqueAtual(produtoAntigo.getEstoqueAtual() + itemAntigo.getQuantidade());
            produtoRepository.save(produtoAntigo);
        }

        venda.setDataVenda(dto.getDataVenda());
        venda.setDataVencimento(dto.getDataVencimento());
        venda.setStatusPagamento(StatusPagamento.valueOf(dto.getStatusPagamento()));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        venda.setCliente(cliente);

        venda.setTipoPagamento(dto.getTipoPagamento());
        venda.setNumeroParcelas(dto.getNumeroParcelas());

        List<ItemVenda> itens = dto.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // 🔹 VERIFICAR ESTOQUE
            if (produto.getEstoqueAtual() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(itemDto.getPrecoUnitario());
            item.setVenda(venda);
            return item;
        }).toList();

        venda.setItens(itens);

        BigDecimal valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        venda.setValorTotal(valorTotal);

        Venda atualizada = vendaRepository.save(venda);

        // 🔹 DAR BAIXA NO ESTOQUE DOS NOVOS ITENS
        for (ItemVenda item : atualizada.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        return vendaMapper.toResponseDto(atualizada);
    }


    // Deletar venda
    @Transactional
    public void deletar(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // 🔹 DEVOLVER O ESTOQUE ANTES DE DELETAR
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        vendaRepository.delete(venda);
    }


    // Relatórios
    public BigDecimal relatorioDiario(LocalDate data) {
        return vendaRepository.totalVendasDoDia(data);
    }

    public BigDecimal relatorioMensal(LocalDate inicio, LocalDate fim) {
        return vendaRepository.totalVendasNoPeriodo(inicio, fim);
    }

    public List<RelatorioFormaPagamentoDto> relatorioPorFormaPagamento() {
        return vendaRepository.totalPorFormaPagamento().stream()
                .map(obj -> new RelatorioFormaPagamentoDto(
                        (String) obj[0],   // 🔹 String direto, NÃO enum
                        (BigDecimal) obj[1]
                ))
                .toList();
    }


    public List<RelatorioClienteDto> relatorioPorCliente() {
        return vendaRepository.totalPorCliente().stream()
                .map(obj -> new RelatorioClienteDto(
                        (String) obj[0],
                        (String) obj[1],
                        (BigDecimal) obj[2]
                ))
                .toList();
    }

    public List<RelatorioValoresAReceberDto> relatorioValoresAReceber() {
        return vendaRepository.valoresAReceber(LocalDate.now()).stream()
                .map(v -> new RelatorioValoresAReceberDto(
                        v.getCliente().getNome(),
                        v.getCliente().getCpf(),
                        v.getValorTotal(),
                        v.getDataVencimento()
                ))
                .toList();
    }

    public List<RelatorioInadimplenciaDto> relatorioInadimplencia() {
        LocalDate hoje = LocalDate.now();
        return vendaRepository.inadimplentes(hoje).stream()
                .map(v -> new RelatorioInadimplenciaDto(
                        v.getCliente().getNome(),
                        v.getCliente().getCpf(),
                        v.getValorTotal(),
                        v.getDataVencimento(),
                        java.time.temporal.ChronoUnit.DAYS.between(v.getDataVencimento(), hoje)
                ))
                .toList();
    }

    @Transactional
    public void atualizarStatusPagamento(Long vendaId, StatusPagamento novoStatus) {
        vendaRepository.findById(vendaId).ifPresent(venda -> {
            venda.setStatusPagamento(novoStatus);
            vendaRepository.save(venda);
        });
    }

    public Page<VendaResponseDto> buscarPorPeriodo(LocalDate inicio, LocalDate fim, int page) {
        Page<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }
    public Page<VendaResponseDto> buscarPorFormaPagamento(String tipoPagamento, int page) {
        // NÃO converte para enum, usa String direto
        Page<Venda> vendas = vendaRepository.findByTipoPagamento(tipoPagamento, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    public Page<VendaResponseDto> buscarPorFormaPagamentoEStatus(String tipoPagamento, StatusPagamento status, int page) {
        // NÃO converte para enum, usa String direto
        Page<Venda> vendas = vendaRepository.findByTipoPagamentoAndStatusPagamento(tipoPagamento, status, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }

    public Page<VendaResponseDto> buscarComFiltros(String tipoPagamento, String status, int page) {
        try {
            if (tipoPagamento != null && status != null) {
                StatusPagamento statusEnum = StatusPagamento.valueOf(status.toUpperCase());
                // NÃO converte tipoPagamento para enum
                Page<Venda> vendas = vendaRepository.findByTipoPagamentoAndStatusPagamento(
                        tipoPagamento, statusEnum, PageRequest.of(page, 8)
                );
                return vendas.map(vendaMapper::toResponseDto);
            } else if (tipoPagamento != null) {
                return buscarPorFormaPagamento(tipoPagamento, page);
            } else if (status != null) {
                StatusPagamento statusEnum = StatusPagamento.valueOf(status.toUpperCase());
                return buscarPorStatus(statusEnum, page);
            } else {
                return listarPaginado(page);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Parâmetro inválido: " + e.getMessage());
        }
    }

    public Page<VendaResponseDto> buscarPorStatus(StatusPagamento status, int page) {
        Page<Venda> vendas = vendaRepository.findByStatusPagamento(status, PageRequest.of(page, 8));
        return vendas.map(vendaMapper::toResponseDto);
    }





}