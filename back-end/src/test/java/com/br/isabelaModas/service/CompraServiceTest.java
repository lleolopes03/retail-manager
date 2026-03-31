package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.CompraRequestDto;
import com.br.isabelaModas.dtos.CompraResponseDto;
import com.br.isabelaModas.dtos.FornecedorResponseDto;
import com.br.isabelaModas.dtos.ItemCompraDto;
import com.br.isabelaModas.dtos.mapper.CompraMapper;
import com.br.isabelaModas.entity.Compra;
import com.br.isabelaModas.entity.Fornecedor;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.repository.CompraRepository;
import com.br.isabelaModas.repository.FornecedorRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private CompraMapper compraMapper;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CompraService service;

    private Fornecedor fornecedor;
    private Produto produto;
    private Compra compra;
    private CompraRequestDto requestDto;
    private CompraResponseDto responseDto;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setNome("Fornecedor Teste");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Camisa");
        produto.setEstoqueAtual(5);

        ItemCompraDto item1 = new ItemCompraDto();
        item1.setProdutoId(1L);
        item1.setQuantidade(2);
        item1.setPrecoUnitario(new BigDecimal("10.00"));

        ItemCompraDto item2 = new ItemCompraDto();
        item2.setProdutoId(1L);
        item2.setQuantidade(1);
        item2.setPrecoUnitario(new BigDecimal("20.00"));

        requestDto = new CompraRequestDto();
        requestDto.setDataCompra(LocalDate.now());
        requestDto.setFornecedorId(1L);
        requestDto.setItens(List.of(item1, item2));

        compra = new Compra();
        compra.setId(1L);
        compra.setDataCompra(requestDto.getDataCompra());
        compra.setFornecedor(fornecedor);
        compra.setValorTotal(new BigDecimal("40.00"));

        responseDto = new CompraResponseDto();
        responseDto.setId(1L);
        responseDto.setDataCompra(requestDto.getDataCompra());
        responseDto.setValorTotal(new BigDecimal("40.00"));
        responseDto.setFornecedor(new FornecedorResponseDto());
    }

    @Test
    void deveCriarCompraComSucesso() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(compraMapper.toEntity(requestDto)).thenReturn(compra);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(compraRepository.save(compra)).thenReturn(compra);
        when(compraMapper.toResponseDto(compra)).thenReturn(responseDto);

        CompraResponseDto result = service.criar(requestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("40.00"), result.getValorTotal());
        verify(compraRepository, times(1)).save(compra);
        verify(produtoRepository, times(2)).save(any(Produto.class));
    }

    @Test
    void deveIncrementarEstoqueAoCriarCompra() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(compraMapper.toEntity(requestDto)).thenReturn(compra);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(compraRepository.save(compra)).thenReturn(compra);
        when(compraMapper.toResponseDto(compra)).thenReturn(responseDto);

        service.criar(requestDto);

        assertEquals(8, produto.getEstoqueAtual());
    }

    @Test
    void deveLancarExcecaoSeFornecedorNaoExistir() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.criar(requestDto));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoSeProdutoNaoExistir() {
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(compraMapper.toEntity(requestDto)).thenReturn(compra);
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.criar(requestDto));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void deveBuscarCompraPorId() {
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));
        when(compraMapper.toResponseDto(compra)).thenReturn(responseDto);

        CompraResponseDto result = service.buscarPorId(1L);

        assertEquals(new BigDecimal("40.00"), result.getValorTotal());
    }

    @Test
    void deveLancarExcecaoAoBuscarCompraInexistente() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
        assertEquals("Compra não encontrada", ex.getMessage());
    }

    @Test
    void deveListarTodasCompras() {
        when(compraRepository.findAll()).thenReturn(List.of(compra));
        when(compraMapper.toResponseDtoList(List.of(compra))).thenReturn(List.of(responseDto));

        List<CompraResponseDto> result = service.listarTodas();

        assertEquals(1, result.size());
        verify(compraRepository, times(1)).findAll();
    }

    @Test
    void deveDeletarCompraComSucesso() {
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        service.deletar(1L);

        verify(compraRepository, times(1)).delete(compra);
    }

    @Test
    void deveLancarExcecaoAoDeletarCompraInexistente() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deletar(99L));
        verify(compraRepository, never()).delete(any());
    }
}
