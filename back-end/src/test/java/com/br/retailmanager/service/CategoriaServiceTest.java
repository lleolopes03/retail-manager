package com.br.retailmanager.service;

import com.br.retailmanager.dtos.CategoriaRequestDto;
import com.br.retailmanager.dtos.CategoriaResponseDto;
import com.br.retailmanager.dtos.mapper.CategoriaMapper;
import com.br.retailmanager.entity.Categoria;
import com.br.retailmanager.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CategoriaServiceTest {
    @Mock
    private CategoriaRepository repository;

    @Mock
    private CategoriaMapper mapper;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;
    private CategoriaRequestDto requestDto;
    private CategoriaResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Roupas");
        categoria.setDescricao("Categoria de roupas");

        requestDto = new CategoriaRequestDto();
        requestDto.setNome("Roupas");
        requestDto.setDescricao("Categoria de roupas");

        responseDto = new CategoriaResponseDto();
        responseDto.setId(1L);
        responseDto.setNome("Roupas");
        responseDto.setDescricao("Categoria de roupas");
    }

    @Test
    void deveCriarCategoria() {
        when(mapper.toEntity(requestDto)).thenReturn(categoria);
        when(repository.save(categoria)).thenReturn(categoria);
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        CategoriaResponseDto result = service.criar(requestDto);

        assertEquals("Roupas", result.getNome());
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void deveBuscarCategoriaPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        CategoriaResponseDto result = service.buscarPorId(1L);

        assertEquals("Roupas", result.getNome());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveListarCategorias() {
        when(repository.findAll()).thenReturn(Arrays.asList(categoria));
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        var result = service.listar();

        assertEquals(1, result.size());
        assertEquals("Roupas", result.get(0).getNome());
    }
    @Test
    void deveBuscarCategoriaPorNome() {
        when(repository.findByNome("Roupas")).thenReturn(Optional.of(categoria));
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        CategoriaResponseDto result = service.buscarPorNome("Roupas");

        assertEquals("Roupas", result.getNome());
        verify(repository, times(1)).findByNome("Roupas");
    }

    @Test
    void deveBuscarCategoriaPorNomeParcial() {
        when(repository.findByNomeContainingIgnoreCase("Rou")).thenReturn(Arrays.asList(categoria));
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        var result = service.buscarPorNomeParcial("Rou");

        assertEquals(1, result.size());
        assertEquals("Roupas", result.get(0).getNome());
    }

    @Test
    void deveAtualizarCategoria() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        doAnswer(invocation -> {
            CategoriaRequestDto dto = invocation.getArgument(0);
            Categoria entity = invocation.getArgument(1);
            entity.setNome(dto.getNome());
            return null;
        }).when(mapper).updateEntityFromDto(any(CategoriaRequestDto.class), eq(categoria));
        when(repository.save(categoria)).thenReturn(categoria);
        when(mapper.toDto(categoria)).thenReturn(responseDto);

        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Atualizada");
        dto.setDescricao("Nova descrição");

        CategoriaResponseDto result = service.atualizar(1L, dto);

        assertEquals("Roupas", result.getNome()); // porque o mapper.toDto retorna responseDto mockado
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void deveDeletarCategoria() {
        doNothing().when(repository).deleteById(1L);

        service.deletar(1L);

        verify(repository, times(1)).deleteById(1L);
    }


}
