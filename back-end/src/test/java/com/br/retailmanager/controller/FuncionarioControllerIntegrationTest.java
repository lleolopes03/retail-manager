package com.br.retailmanager.controller;

import com.br.retailmanager.TestSecurityConfig;
import com.br.retailmanager.dtos.FuncionarioRequestDto;
import com.br.retailmanager.dtos.FuncionarioResponseDto;
import com.br.retailmanager.entity.enums.Cargo;
import com.br.retailmanager.entity.enums.Perfil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class FuncionarioControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FuncionarioRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new FuncionarioRequestDto();
        requestDto.setNome("Leandro");
        requestDto.setCpf("11144477735");
        requestDto.setEmail("leandro@email.com");
        requestDto.setTelefone("31999999999");
        requestDto.setDataContratacao(LocalDate.now());
        requestDto.setSalario(BigDecimal.valueOf(2500));
        requestDto.setCargo(Cargo.VENDEDOR);
        requestDto.setLogin("leandro");
        requestDto.setSenha("123456");
        requestDto.setPerfil(Perfil.VENDEDOR);
    }

    @Test
    void deveCriarFuncionarioComSucesso() throws Exception {
        mockMvc.perform(post("/api/v1/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Leandro"))
                .andExpect(jsonPath("$.cpf").value("11144477735"));
    }

    @Test
    void deveListarFuncionarios() throws Exception {
        // Primeiro cria um funcionário
        mockMvc.perform(post("/api/v1/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Depois lista todos
        mockMvc.perform(get("/api/v1/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Leandro"));
    }

    @Test
    void deveBuscarFuncionarioPorCpf() throws Exception {
        // Cria funcionário
        mockMvc.perform(post("/api/v1/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Busca por CPF
        mockMvc.perform(get("/api/v1/funcionarios/cpf/{cpf}", "11144477735"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Leandro"));
    }

    @Test
    void deveAtualizarFuncionario() throws Exception {
        // Cria funcionário
        MvcResult result = mockMvc.perform(post("/api/v1/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        FuncionarioResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FuncionarioResponseDto.class
        );

        // Atualiza nome
        requestDto.setNome("Leandro Atualizado");

        mockMvc.perform(put("/api/v1/funcionarios/{id}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Leandro Atualizado"));
    }

    @Test
    void deveDeletarFuncionario() throws Exception {
        // Cria funcionário
        MvcResult result = mockMvc.perform(post("/api/v1/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn();

        FuncionarioResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FuncionarioResponseDto.class
        );

        // Deleta
        mockMvc.perform(delete("/api/v1/funcionarios/{id}", response.getId()))
                .andExpect(status().isNoContent());
    }


}
