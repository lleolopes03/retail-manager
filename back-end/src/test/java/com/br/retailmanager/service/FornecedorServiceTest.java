package com.br.retailmanager.service;

import com.br.retailmanager.dtos.EnderecoDto;
import com.br.retailmanager.dtos.FornecedorRequestDto;
import com.br.retailmanager.dtos.FornecedorResponseDto;
import com.br.retailmanager.dtos.mapper.FornecedorMapper;
import com.br.retailmanager.entity.Endereco;
import com.br.retailmanager.entity.Fornecedor;
import com.br.retailmanager.repository.FornecedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {
    @Mock
    private FornecedorRepository repository;

    @Mock
    private FornecedorMapper mapper;

    @InjectMocks
    private FornecedorService service;

    private Fornecedor fornecedor;
    private FornecedorRequestDto requestDto;
    private FornecedorResponseDto responseDto;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setNome("Fornecedor Teste");
        fornecedor.setCnpj("12345678000195");
        fornecedor.setEmail("fornecedor@email.com");
        fornecedor.setTelefone("31999999999");
        fornecedor.setEndereco(new Endereco("32604123", "Rua A", "Apto 1", "Centro", "Betim", "MG"));

        requestDto = new FornecedorRequestDto();
        requestDto.setNome("Fornecedor Teste");
        requestDto.setCnpj("12345678000195");
        requestDto.setEmail("fornecedor@email.com");
        requestDto.setTelefone("31999999999");
        requestDto.setEndereco(new EnderecoDto("32604123", "Rua A", "Apto 1", "Centro", "Betim", "MG"));

        responseDto = new FornecedorResponseDto();
        responseDto.setId(1L);
        responseDto.setNome("Fornecedor Teste");
        responseDto.setCnpj("12345678000195");
        responseDto.setEmail("fornecedor@email.com");
        responseDto.setTelefone("31999999999");
        responseDto.setEndereco(new EnderecoDto("32604123", "Rua A", "Apto 1", "Centro", "Betim", "MG"));
    }

    @Test
    void deveCriarFornecedorComSucesso() {
        when(repository.existsByCnpj(requestDto.getCnpj())).thenReturn(false);
        when(repository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(mapper.toEntity(requestDto)).thenReturn(fornecedor);
        when(repository.save(fornecedor)).thenReturn(fornecedor);
        when(mapper.toDTO(fornecedor)).thenReturn(responseDto);

        FornecedorResponseDto result = service.criar(requestDto);

        assertNotNull(result);
        assertEquals("Fornecedor Teste", result.getNome());
        verify(repository, times(1)).save(fornecedor);
    }

    @Test
    void deveLancarExcecaoAoCriarFornecedorComCnpjDuplicado() {
        when(repository.existsByCnpj(requestDto.getCnpj())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.criar(requestDto));
    }

    @Test
    void deveBuscarFornecedorPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(mapper.toDTO(fornecedor)).thenReturn(responseDto);

        FornecedorResponseDto result = service.buscarPorId(1L);

        assertEquals("Fornecedor Teste", result.getNome());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarFornecedorPorIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveAtualizarFornecedorComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(repository.save(fornecedor)).thenReturn(fornecedor);
        when(mapper.toDTO(fornecedor)).thenReturn(responseDto);
        responseDto.setNome("Fornecedor Atualizado");


        requestDto.setNome("Fornecedor Atualizado");
        fornecedor.setNome("Fornecedor Atualizado");

        when(mapper.toEntity(requestDto)).thenReturn(fornecedor);

        FornecedorResponseDto result = service.atualizar(1L, requestDto);

        assertEquals("Fornecedor Atualizado", result.getNome());
        verify(repository, times(1)).save(fornecedor);
    }

    @Test
    void deveDeletarFornecedorComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(fornecedor));

        service.deletar(1L);

        verify(repository, times(1)).delete(fornecedor);
    }

    @Test
    void deveLancarExcecaoAoDeletarFornecedorInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deletar(99L));
    }


}
