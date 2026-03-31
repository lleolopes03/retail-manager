package com.br.isabelaModas.controller;

import com.br.isabelaModas.TestSecurityConfig;
import com.br.isabelaModas.dtos.CategoriaRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarCategoria() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Calçados");
        dto.setDescricao("Categoria de calçados");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Calçados"));
    }

    @Test
    void deveListarCategorias() throws Exception {
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Acessórios");
        dto.setDescricao("Categoria de acessórios");

        String response = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/categorias/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Acessórios"));
    }

    @Test
    void deveBuscarCategoriaPorNome() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Infantil");
        dto.setDescricao("Categoria infantil");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/categorias/nome/Infantil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Infantil"));
    }

    @Test
    void deveBuscarCategoriaPorNomeParcial() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Moda Feminina");
        dto.setDescricao("Categoria feminina");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/categorias/search")
                        .param("nome", "Moda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Moda Feminina"));
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Esporte");
        dto.setDescricao("Categoria esportiva");

        String response = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        dto.setNome("Esporte Atualizado");

        mockMvc.perform(put("/api/v1/categorias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Esporte Atualizado"));
    }

    @Test
    void deveDeletarCategoria() throws Exception {
        CategoriaRequestDto dto = new CategoriaRequestDto();
        dto.setNome("Promoções");
        dto.setDescricao("Categoria de promoções");

        String response = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/categorias/" + id))
                .andExpect(status().isNoContent());
    }


}
