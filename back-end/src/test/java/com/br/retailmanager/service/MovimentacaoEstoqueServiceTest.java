package com.br.retailmanager.service;

import com.br.retailmanager.dtos.MovimentacaoEstoqueRequestDto;
import com.br.retailmanager.dtos.MovimentacaoEstoqueResponseDto;
import com.br.retailmanager.dtos.mapper.MovimentacaoEstoqueMapper;
import com.br.retailmanager.entity.Cliente;
import com.br.retailmanager.entity.MovimentacaoEstoque;
import com.br.retailmanager.entity.Produto;
import com.br.retailmanager.repository.ClienteRepository;
import com.br.retailmanager.repository.MovimentacaoEstoqueRepository;
import com.br.retailmanager.repository.ProdutoRepository;
import com.br.retailmanager.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoEstoqueServiceTest {
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    @Mock
    private MovimentacaoEstoqueMapper mapper;

    @InjectMocks
    private MovimentacaoEstoqueService service;

    @Test
    void deveRegistrarSaidaTemporaria() {
        Produto produto = new Produto(1L, "Camisa", BigDecimal.TEN, "M", "Azul", 10, null);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        MovimentacaoEstoqueRequestDto dto = new MovimentacaoEstoqueRequestDto();
        dto.setProdutoId(1L);
        dto.setClienteId(1L);
        dto.setQuantidade(5);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        when(mapper.toEntity(dto)).thenReturn(mov);
        when(mapper.toResponseDto(mov)).thenReturn(new MovimentacaoEstoqueResponseDto());

        MovimentacaoEstoqueResponseDto response = service.registrarSaidaTemporaria(dto);

        assertEquals(5, produto.getEstoqueAtual()); // estoque atualizado
        verify(movimentacaoEstoqueRepository).save(mov);
        verify(produtoRepository).save(produto);
        assertNotNull(response);
    }

    @Test
    void deveRegistrarDevolucao() {
        Produto produto = new Produto(1L, "Camisa", BigDecimal.TEN, "M", "Azul", 5, null);
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        MovimentacaoEstoqueRequestDto dto = new MovimentacaoEstoqueRequestDto();
        dto.setProdutoId(1L);
        dto.setClienteId(1L);
        dto.setQuantidade(3);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        when(mapper.toEntity(dto)).thenReturn(mov);
        when(mapper.toResponseDto(mov)).thenReturn(new MovimentacaoEstoqueResponseDto());

        MovimentacaoEstoqueResponseDto response = service.registrarDevolucao(dto);

        assertEquals(8, produto.getEstoqueAtual()); // estoque devolvido
        verify(movimentacaoEstoqueRepository).save(mov);
        verify(produtoRepository).save(produto);
        assertNotNull(response);
    }


}
