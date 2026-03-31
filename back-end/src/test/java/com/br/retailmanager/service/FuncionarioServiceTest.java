package com.br.retailmanager.service;

import com.br.retailmanager.dtos.FuncionarioRequestDto;
import com.br.retailmanager.dtos.FuncionarioResponseDto;
import com.br.retailmanager.dtos.mapper.FuncionarioMapper;
import com.br.retailmanager.entity.Funcionario;
import com.br.retailmanager.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private FuncionarioMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FuncionarioService service;

    private Funcionario funcionario;
    private FuncionarioRequestDto requestDto;
    private FuncionarioResponseDto responseDto;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Leandro");
        funcionario.setCpf("12345678900");
        funcionario.setEmail("leandro@email.com");
        funcionario.setTelefone("31999999999");

        requestDto = new FuncionarioRequestDto();
        requestDto.setNome("Leandro");
        requestDto.setCpf("12345678900");
        requestDto.setEmail("leandro@email.com");
        requestDto.setTelefone("31999999999");
        requestDto.setSenha("senha123");

        responseDto = new FuncionarioResponseDto();
        responseDto.setId(1L);
        responseDto.setNome("Leandro");
        responseDto.setCpf("12345678900");
        responseDto.setEmail("leandro@email.com");
        responseDto.setTelefone("31999999999");
    }

    @Test
    void deveCriarFuncionarioComSucesso() {
        when(mapper.toEntity(requestDto)).thenReturn(funcionario);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(repository.save(funcionario)).thenReturn(funcionario);
        when(mapper.toResponseDto(funcionario)).thenReturn(responseDto);

        FuncionarioResponseDto result = service.criar(requestDto);

        assertNotNull(result);
        assertEquals("Leandro", result.getNome());
        verify(repository, times(1)).save(funcionario);
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void deveBuscarFuncionarioPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(mapper.toResponseDto(funcionario)).thenReturn(responseDto);

        FuncionarioResponseDto result = service.buscarPorId(1L);

        assertEquals("Leandro", result.getNome());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarFuncionarioPorIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
        assertEquals("Funcionário não encontrado", ex.getMessage());
    }

    @Test
    void deveAtualizarFuncionarioComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(repository.save(funcionario)).thenReturn(funcionario);
        when(mapper.toResponseDto(funcionario)).thenReturn(responseDto);

        FuncionarioResponseDto result = service.atualizar(1L, requestDto);

        assertEquals("Leandro", result.getNome());
        verify(repository, times(1)).save(funcionario);
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void deveDeletarFuncionarioComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));

        service.deletar(1L);

        verify(repository, times(1)).delete(funcionario);
    }

    @Test
    void deveLancarExcecaoAoDeletarFuncionarioInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deletar(99L));
        assertEquals("Funcionário não encontrado", ex.getMessage());
    }

    @Test
    void deveBuscarFuncionarioPorCpf() {
        when(repository.findByCpf("12345678900")).thenReturn(Optional.of(funcionario));
        when(mapper.toResponseDto(funcionario)).thenReturn(responseDto);

        FuncionarioResponseDto result = service.buscarPorCpf("12345678900");

        assertEquals("Leandro", result.getNome());
    }

    @Test
    void deveLancarExcecaoAoBuscarPorCpfInexistente() {
        when(repository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorCpf("00000000000"));
    }
}
