package com.br.retailmanager.controller;

import com.br.retailmanager.NoSecurityConfig;
import com.br.retailmanager.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // se precisar mockar algo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.br.retailmanager.dtos.FornecedorRequestDto;
import com.br.retailmanager.dtos.FornecedorResponseDto;
import com.br.retailmanager.dtos.EnderecoDto;



@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Transactional
public class FornecedorControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FornecedorRequestDto requestDto;

    @BeforeEach
    void setUp() {
        EnderecoDto endereco = new EnderecoDto();
        endereco.setCep("32604123");
        endereco.setLogradouro("Rua A");
        endereco.setComplemento("Apto 1");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Betim");
        endereco.setUf("MG");

        requestDto = new FornecedorRequestDto();
        requestDto.setNome("Fornecedor Teste");
        requestDto.setCnpj("27865757000102"); // CNPJ válido
        requestDto.setEmail("fornecedor@email.com");
        requestDto.setTelefone("31999999999");
        requestDto.setEndereco(endereco);
    }

    @Test
    void deveCriarFornecedorComSucesso() throws Exception {
        mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Fornecedor Teste"))
                .andExpect(jsonPath("$.cnpj").value("27865757000102"));
    }

    @Test
    void deveListarFornecedores() throws Exception {
        // cria fornecedor
        mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // lista todos
        mockMvc.perform(get("/api/v1/fornecedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Fornecedor Teste"));
    }

    @Test
    void deveBuscarFornecedorPorCnpj() throws Exception {
        // cria fornecedor
        mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // busca por CNPJ
        mockMvc.perform(get("/api/v1/fornecedores/cnpj/{cnpj}", "27865757000102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fornecedor Teste"));
    }

    @Test
    void deveAtualizarFornecedor() throws Exception {
        // cria fornecedor
        MvcResult result = mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        FornecedorResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FornecedorResponseDto.class
        );

        // atualiza nome
        requestDto.setNome("Fornecedor Atualizado");

        mockMvc.perform(put("/api/v1/fornecedores/{id}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fornecedor Atualizado"));
    }

    @Test
    void deveDeletarFornecedor() throws Exception {
        // cria fornecedor
        MvcResult result = mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        FornecedorResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FornecedorResponseDto.class
        );

        // deleta
        mockMvc.perform(delete("/api/v1/fornecedores/{id}", response.getId()))
                .andExpect(status().isNoContent());
    }


}
