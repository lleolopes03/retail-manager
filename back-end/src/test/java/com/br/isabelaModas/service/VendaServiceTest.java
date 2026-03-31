package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.ItemVendaDto;
import com.br.isabelaModas.dtos.VendaRequestDto;
import com.br.isabelaModas.dtos.VendaResponseDto;
import com.br.isabelaModas.dtos.mapper.VendaMapper;
import com.br.isabelaModas.entity.Cliente;
import com.br.isabelaModas.entity.ItemVenda;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.entity.Venda;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.repository.ClienteRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import com.br.isabelaModas.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VendaMapper vendaMapper;

    @Mock
    private ParcelaService parcelaService;

    @InjectMocks
    private VendaService service;

    private Cliente cliente;
    private Produto produto;
    private Venda venda;
    private VendaRequestDto requestDto;
    private VendaResponseDto responseDto;
    private ItemVendaDto itemDto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Ana Paula");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Camisa Floral");
        produto.setEstoqueAtual(10);

        itemDto = new ItemVendaDto();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(2);
        itemDto.setPrecoUnitario(new BigDecimal("50.00"));

        requestDto = new VendaRequestDto();
        requestDto.setClienteId(1L);
        requestDto.setDataVenda(LocalDate.now());
        requestDto.setDataVencimento(LocalDate.now().plusDays(30));
        requestDto.setStatusPagamento("PENDENTE");
        requestDto.setTipoPagamento("DINHEIRO");
        requestDto.setNumeroParcelas(1);
        requestDto.setItens(List.of(itemDto));

        venda = new Venda();
        venda.setId(1L);
        venda.setItens(new ArrayList<>());

        responseDto = new VendaResponseDto();
        responseDto.setId(1L);
        responseDto.setValorTotal(new BigDecimal("100.00"));
        responseDto.setStatusPagamento("PENDENTE");
    }

    @Test
    void deveCriarVendaComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        VendaResponseDto resultado = service.criar(requestDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(new BigDecimal("100.00"), resultado.getValorTotal());
        verify(vendaRepository).save(any(Venda.class));
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    void deveDarBaixaNoEstoqueAoCriarVenda() {
        produto.setEstoqueAtual(10);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        service.criar(requestDto);

        assertEquals(8, produto.getEstoqueAtual());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(requestDto));

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(vendaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(requestDto));

        assertEquals("Produto não encontrado", ex.getMessage());
        verify(vendaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        produto.setEstoqueAtual(1);
        itemDto.setQuantidade(5);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(requestDto));

        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        verify(vendaRepository, never()).save(any());
    }

    @Test
    void deveCriarVendaCarneEGerarParcelas() {
        requestDto.setTipoPagamento("CARNE");
        requestDto.setNumeroParcelas(3);
        requestDto.setPrimeiraParcela(LocalDate.now());
        requestDto.setIntervaloDias(30);

        venda.setTipoPagamento("CARNE");
        venda.setNumeroParcelas(3);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        service.criar(requestDto);

        verify(parcelaService).gerarParcelas(eq(venda), eq(3), any(LocalDate.class), anyInt());
    }

    @Test
    void naoDeveGerarParcelasParaVendaNaoCarne() {
        requestDto.setTipoPagamento("DINHEIRO");
        requestDto.setNumeroParcelas(1);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(vendaMapper.toEntity(requestDto)).thenReturn(venda);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        service.criar(requestDto);

        verify(parcelaService, never()).gerarParcelas(any(Venda.class), anyInt(), any(LocalDate.class), anyInt());
    }

    @Test
    void deveBuscarVendaPorId() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        VendaResponseDto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarVendaInexistente() {
        when(vendaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
        assertEquals("Venda não encontrada", ex.getMessage());
    }

    @Test
    void deveDeletarVendaDevolvEndoEstoque() {
        Produto produtoItem = new Produto();
        produtoItem.setId(1L);
        produtoItem.setEstoqueAtual(8);

        ItemVenda item = new ItemVenda();
        item.setProduto(produtoItem);
        item.setQuantidade(2);
        venda.setItens(List.of(item));

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoItem);

        service.deletar(1L);

        assertEquals(10, produtoItem.getEstoqueAtual());
        verify(vendaRepository).delete(venda);
    }

    @Test
    void deveLancarExcecaoAoDeletarVendaInexistente() {
        when(vendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deletar(99L));
        verify(vendaRepository, never()).delete(any());
    }

    @Test
    void deveAtualizarStatusPagamento() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);

        service.atualizarStatusPagamento(1L, StatusPagamento.PAGO);

        assertEquals(StatusPagamento.PAGO, venda.getStatusPagamento());
        verify(vendaRepository).save(venda);
    }

    @Test
    void deveListarTodasVendas() {
        when(vendaRepository.findAll()).thenReturn(List.of(venda));
        when(vendaMapper.toResponseDtoList(List.of(venda))).thenReturn(List.of(responseDto));

        List<VendaResponseDto> resultado = service.listarTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void deveBuscarComFiltroSomenteTipoPagamento() {
        Page<Venda> page = new PageImpl<>(List.of(venda));
        when(vendaRepository.findByTipoPagamento(eq("DINHEIRO"), any(PageRequest.class))).thenReturn(page);
        when(vendaMapper.toResponseDto(venda)).thenReturn(responseDto);

        Page<VendaResponseDto> resultado = service.buscarComFiltros("DINHEIRO", null, 0);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveBuscarComFiltroStatusInvalidoLancaExcecao() {
        assertThrows(RuntimeException.class, () -> service.buscarComFiltros(null, "STATUS_INVALIDO", 0));
    }
}
