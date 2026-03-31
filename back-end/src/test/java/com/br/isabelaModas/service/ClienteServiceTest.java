package com.br.isabelaModas.service;

import com.br.isabelaModas.dtos.ClienteRequestDto;
import com.br.isabelaModas.dtos.ClienteResponseDto;
import com.br.isabelaModas.dtos.mapper.ClienteMapper;
import com.br.isabelaModas.entity.Cliente;
import com.br.isabelaModas.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class ClienteServiceTest {
    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;
    private ClienteRequestDto requestDto;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Leandro");
        cliente.setCpf("12345678901");
        cliente.setEmail("leandro@email.com");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));

        requestDto = new ClienteRequestDto();
        requestDto.setNome("Leandro");
        requestDto.setCpf("12345678901");
        requestDto.setEmail("leandro@email.com");
        requestDto.setDataNascimento(LocalDate.of(1990, 1, 1));

        responseDto = new ClienteResponseDto();
        responseDto.setId(1L);
        responseDto.setNome("Leandro");
        responseDto.setCpf("12345678901");
        responseDto.setEmail("leandro@email.com");
        responseDto.setDataNascimento(LocalDate.of(1990, 1, 1));
    }

    @Test
    void deveCriarClienteComCpfUnico() {
        when(repository.existsByCpf(requestDto.getCpf())).thenReturn(false);
        when(mapper.toEntity(requestDto)).thenReturn(cliente);
        when(repository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto result = service.criar(requestDto);

        assertEquals("Leandro", result.getNome());
        verify(repository, times(1)).save(cliente);
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComCpfDuplicado() {
        when(repository.existsByCpf(requestDto.getCpf())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(requestDto));
        assertEquals("Já existe cliente com este CPF", ex.getMessage());
    }

    @Test
    void deveBuscarClientePorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto result = service.buscarPorId(1L);

        assertEquals("Leandro", result.getNome());
    }

    @Test
    void deveBuscarClientePorCpf() {
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto result = service.buscarPorCpf("12345678901");

        assertEquals("Leandro", result.getNome());
    }

    @Test
    void deveBuscarClientePorEmail() {
        when(repository.findByEmail("leandro@email.com")).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto result = service.buscarPorEmail("leandro@email.com");

        assertEquals("Leandro", result.getNome());
    }

    @Test
    void deveBuscarClientePorNome() {
        when(repository.findByNome("Leandro")).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);

        ClienteResponseDto result = service.buscarPorNome("Leandro");

        assertEquals("Leandro", result.getNome());
    }

    @Test
    void deveBuscarPorNomeParcial() {
        when(repository.findByNomeContainingIgnoreCase("Lean")).thenReturn(Arrays.asList(cliente));
        when(mapper.toResponseDto(cliente)).thenReturn(responseDto);
        when(mapper.toResponseDtoList(List.of(cliente))).thenReturn(List.of(responseDto));

        var result = service.buscarPorNomeParcial("Lean");

        assertEquals(1, result.size());
        assertEquals("Leandro", result.get(0).getNome());
    }

    @Test
    void deveListarTodosClientes() {
        when(repository.findAll()).thenReturn(List.of(cliente));
        when(mapper.toResponseDtoList(List.of(cliente))).thenReturn(List.of(responseDto));

        List<ClienteResponseDto> result = service.listar();

        assertEquals(1, result.size());
        assertEquals("Leandro", result.get(0).getNome());

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toResponseDtoList(List.of(cliente));
    }

}
