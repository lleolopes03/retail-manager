package com.br.retailmanager.service;

import com.br.retailmanager.dtos.ProdutoRequestDto;
import com.br.retailmanager.dtos.ProdutoResponseDto;
import com.br.retailmanager.dtos.mapper.ProdutoMapper;
import com.br.retailmanager.entity.Categoria;
import com.br.retailmanager.entity.Produto;
import com.br.retailmanager.repository.CategoriaRepository;
import com.br.retailmanager.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {
    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoMapper mapper;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;
    private Categoria categoria;
    private ProdutoRequestDto requestDto;
    private ProdutoResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Roupas");
        categoria.setDescricao("Categoria de roupas");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Camisa Polo");
        produto.setPreco(BigDecimal.valueOf(99.90));
        produto.setTamanho("M");
        produto.setCor("Azul");
        produto.setEstoqueAtual(10);
        produto.setCategoria(categoria);

        requestDto = new ProdutoRequestDto();
        requestDto.setNome("Camisa Polo");
        requestDto.setPreco(BigDecimal.valueOf(99.90));
        requestDto.setTamanho("M");
        requestDto.setCor("Azul");
        requestDto.setEstoqueAtual(10);
        requestDto.setCategoriaId(1L);

        responseDto = new ProdutoResponseDto();
        responseDto.setId(1L);
        responseDto.setNome("Camisa Polo");
        responseDto.setPreco(BigDecimal.valueOf(99.90));
        responseDto.setTamanho("M");
        responseDto.setCor("Azul");
        responseDto.setEstoqueAtual(10);
    }

    @Test
    void deveCriarProduto() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(mapper.toEntity(requestDto)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(mapper.toDto(produto)).thenReturn(responseDto);

        ProdutoResponseDto result = service.criar(requestDto);

        assertEquals("Camisa Polo", result.getNome());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveListarProdutos() {
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto));
        when(mapper.toDto(produto)).thenReturn(responseDto);

        List<ProdutoResponseDto> result = service.listar();

        assertEquals(1, result.size());
        assertEquals("Camisa Polo", result.get(0).getNome());
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(mapper.toDto(produto)).thenReturn(responseDto);

        ProdutoResponseDto result = service.buscarPorId(1L);

        assertEquals("Camisa Polo", result.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontradoPorId() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveBuscarProdutoPorNome() {
        when(produtoRepository.findByNome("Camisa Polo")).thenReturn(Optional.of(produto));
        when(mapper.toDto(produto)).thenReturn(responseDto);

        ProdutoResponseDto result = service.buscarPorNome("Camisa Polo");

        assertEquals("Camisa Polo", result.getNome());
    }

    @Test
    void deveBuscarProdutoPorNomeParcial() {
        when(produtoRepository.findByNomeContainingIgnoreCase("Camisa")).thenReturn(Arrays.asList(produto));
        when(mapper.toDto(produto)).thenReturn(responseDto);

        List<ProdutoResponseDto> result = service.buscarPorNomeParcial("Camisa");

        assertEquals(1, result.size());
        assertEquals("Camisa Polo", result.get(0).getNome());
    }

    @Test
    void deveBuscarProdutoPorTamanho() {
        when(produtoRepository.findByTamanhoIgnoreCase("M")).thenReturn(Arrays.asList(produto));
        when(mapper.toDto(produto)).thenReturn(responseDto);

        List<ProdutoResponseDto> result = service.buscarPorTamanho("M");

        assertEquals(1, result.size());
        assertEquals("Camisa Polo", result.get(0).getNome());
    }

    @Test
    void deveAtualizarProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria)); // ✅ mock da categoria

        doAnswer(invocation -> {
            ProdutoRequestDto dto = invocation.getArgument(0);
            Produto entity = invocation.getArgument(1);
            entity.setNome(dto.getNome());
            return null;
        }).when(mapper).updateEntityFromDto(any(ProdutoRequestDto.class), eq(produto));

        when(produtoRepository.save(produto)).thenReturn(produto);
        when(mapper.toDto(produto)).thenReturn(responseDto);

        requestDto.setNome("Camisa Polo Atualizada");

        ProdutoResponseDto result = service.atualizar(1L, requestDto);

        assertEquals("Camisa Polo", result.getNome()); // mapper.toDto retorna mockado
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveDeletarProduto() {
        doNothing().when(produtoRepository).deleteById(1L);

        service.deletar(1L);

        verify(produtoRepository, times(1)).deleteById(1L);
    }


}
