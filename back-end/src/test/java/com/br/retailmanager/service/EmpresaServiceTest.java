package com.br.retailmanager.service;

import com.br.retailmanager.dtos.EmpresaRequestDto;
import com.br.retailmanager.dtos.EmpresaResponseDto;
import com.br.retailmanager.dtos.EnderecoDto;
import com.br.retailmanager.dtos.mapper.EmpresaMapper;
import com.br.retailmanager.entity.Empresa;
import com.br.retailmanager.entity.Endereco;
import com.br.retailmanager.repository.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)

public class EmpresaServiceTest {
    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper mapper;

    @InjectMocks
    private EmpresaService empresaService;

    private Endereco cadastroEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCep("32600-000");
        endereco.setLogradouro("Rua das Modas");
        endereco.setComplemento("Sala 101");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Betim");
        endereco.setUf("MG");
        return endereco;
    }

    private EnderecoDto cadastroEnderecoDto() {
        EnderecoDto dto = new EnderecoDto();
        dto.setCep("32600-000");
        return dto;
    }

    private Empresa cadastroEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Isabela Modas");
        empresa.setCnpj("12345678000195");
        empresa.setEmail("contato@RetailManager.com");
        empresa.setTelefone("31999999999");
        empresa.setEndereco(cadastroEndereco());
        empresa.setNumero(100);
        return empresa;
    }

    private EmpresaRequestDto cadastroEmpresaRequestDto() {
        EmpresaRequestDto dto = new EmpresaRequestDto();
        dto.setNome("Isabela Modas");
        dto.setCnpj("12345678000195");
        dto.setEmail("contato@RetailManager.com");
        dto.setTelefone("31999999999");
        dto.setEndereco(cadastroEnderecoDto());
        dto.setNumero(100);
        return dto;
    }

    private EmpresaResponseDto cadastroEmpresaResponseDto() {
        EmpresaResponseDto response = new EmpresaResponseDto();
        response.setId(1L);
        response.setNome("Isabela Modas");
        response.setCnpj("12345678000195");
        response.setEmail("contato@RetailManager.com");
        response.setTelefone("31999999999");
        response.setEndereco(cadastroEndereco());
        response.setNumero(100);
        return response;
    }

    // ------------------------
    // Testes unitários
    // ------------------------

    @Test
    void deveSalvarEmpresaComSucesso() {
        EmpresaRequestDto dto = cadastroEmpresaRequestDto();
        Empresa empresa = cadastroEmpresa();
        EmpresaResponseDto responseDto = cadastroEmpresaResponseDto();

        when(mapper.toEntity(dto)).thenReturn(empresa);
        when(empresaRepository.save(empresa)).thenReturn(empresa);
        when(mapper.toDTO(empresa)).thenReturn(responseDto);

        EmpresaResponseDto result = empresaService.salvar(dto);

        assertNotNull(result);
        assertEquals("Isabela Modas", result.getNome());
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    void deveLancarExcecaoAoSalvarEmpresaQuandoJaExiste() {
        EmpresaRequestDto dto = cadastroEmpresaRequestDto();
        when(empresaRepository.count()).thenReturn(1L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empresaService.salvar(dto));

        assertEquals("Já existe uma empresa cadastrada no sistema", exception.getMessage());
    }

    @Test
    void deveAtualizarEmpresaComSucesso() {
        Long id = 1L;
        EmpresaRequestDto dto = cadastroEmpresaRequestDto();
        Empresa empresaExistente = cadastroEmpresa();
        EmpresaResponseDto responseDto = cadastroEmpresaResponseDto();

        when(empresaRepository.findById(id)).thenReturn(Optional.of(empresaExistente));
        doAnswer(invocation -> {
            EmpresaRequestDto dtoArg = invocation.getArgument(0);
            Empresa entityArg = invocation.getArgument(1);
            entityArg.setNome(dtoArg.getNome());
            entityArg.setEmail(dtoArg.getEmail());
            entityArg.setTelefone(dtoArg.getTelefone());
            entityArg.setNumero(dtoArg.getNumero());
            entityArg.getEndereco().setCep(dtoArg.getEndereco().getCep());
            return null;
        }).when(mapper).updateEntityFromDto(eq(dto), eq(empresaExistente));

        when(empresaRepository.save(empresaExistente)).thenReturn(empresaExistente);
        when(mapper.toDTO(empresaExistente)).thenReturn(responseDto);

        EmpresaResponseDto result = empresaService.atualizar(id, dto);

        assertNotNull(result);
        assertEquals("Isabela Modas", result.getNome());
        verify(empresaRepository, times(1)).save(empresaExistente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarEmpresaInexistente() {
        Long id = 99L;
        EmpresaRequestDto dto = cadastroEmpresaRequestDto();

        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empresaService.atualizar(id, dto));

        assertEquals("Empresa não encontrada", exception.getMessage());
    }

    @Test
    void deveBuscarEmpresaComSucesso() {
        Empresa empresa = cadastroEmpresa();
        EmpresaResponseDto responseDto = cadastroEmpresaResponseDto();

        when(empresaRepository.findAll()).thenReturn(Collections.singletonList(empresa));
        when(mapper.toDTO(empresa)).thenReturn(responseDto);

        EmpresaResponseDto result = empresaService.buscarEmpresa();

        assertNotNull(result);
        assertEquals("Isabela Modas", result.getNome());
        assertEquals("32600-000", result.getEndereco().getCep());
    }

    @Test
    void deveLancarExcecaoAoBuscarEmpresaInexistente() {
        when(empresaRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empresaService.buscarEmpresa());

        assertEquals("Nenhuma empresa cadastrada", exception.getMessage());
    }





}
