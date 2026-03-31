package com.br.retailmanager.controller;

import com.br.retailmanager.NoSecurityConfig;
import com.br.retailmanager.TestSecurityConfig;
import com.br.retailmanager.dtos.ProdutoRequestDto;
import com.br.retailmanager.entity.Categoria;
import com.br.retailmanager.repository.CategoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ProdutoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Long categoriaId;

    @BeforeEach
    void setup() {
        // cria uma categoria para associar aos produtos
        Categoria categoria = new Categoria();
        categoria.setNome("Roupas");
        categoria.setDescricao("Categoria de roupas");
        categoriaId = categoriaRepository.save(categoria).getId();
    }

    @Test
    void deveCriarProduto() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Camisa Polo");
        dto.setPreco(BigDecimal.valueOf(99.90));
        dto.setTamanho("M");
        dto.setCor("Azul");
        dto.setEstoqueAtual(10);
        dto.setCategoriaId(categoriaId);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Camisa Polo"))
                .andExpect(jsonPath("$.categoria.nome").value("Roupas"));
    }

    @Test
    void deveListarProdutos() throws Exception {
        mockMvc.perform(get("/api/v1/produtos"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Calça Jeans");
        dto.setPreco(BigDecimal.valueOf(120.00));
        dto.setTamanho("G");
        dto.setCor("Preto");
        dto.setEstoqueAtual(5);
        dto.setCategoriaId(categoriaId);

        String response = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/produtos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Calça Jeans"));
    }

    @Test
    void deveBuscarProdutoPorNome() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Tênis Nike");
        dto.setPreco(BigDecimal.valueOf(350.00));
        dto.setTamanho("42");
        dto.setCor("Branco");
        dto.setEstoqueAtual(3);
        dto.setCategoriaId(categoriaId);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/produtos/nome/Tênis Nike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tênis Nike"));
    }

    @Test
    void deveBuscarProdutoPorNomeParcial() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Vestido Vermelho");
        dto.setPreco(BigDecimal.valueOf(200.00));
        dto.setTamanho("M");
        dto.setCor("Vermelho");
        dto.setEstoqueAtual(2);
        dto.setCategoriaId(categoriaId);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/produtos/search")
                        .param("nome", "Vestido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Vestido Vermelho"));
    }

    @Test
    void deveBuscarProdutoPorTamanho() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Jaqueta Couro");
        dto.setPreco(BigDecimal.valueOf(500.00));
        dto.setTamanho("G");
        dto.setCor("Preto");
        dto.setEstoqueAtual(1);
        dto.setCategoriaId(categoriaId);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/produtos/tamanho/G"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Jaqueta Couro"));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Camisa Básica");
        dto.setPreco(BigDecimal.valueOf(50.00));
        dto.setTamanho("P");
        dto.setCor("Branca");
        dto.setEstoqueAtual(20);
        dto.setCategoriaId(categoriaId);

        String response = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        dto.setNome("Camisa Básica Atualizada");

        mockMvc.perform(put("/api/v1/produtos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Camisa Básica Atualizada"));
    }

    @Test
    void deveDeletarProduto() throws Exception {
        ProdutoRequestDto dto = new ProdutoRequestDto();
        dto.setNome("Boné");
        dto.setPreco(BigDecimal.valueOf(30.00));
        dto.setTamanho("Único");
        dto.setCor("Preto");
        dto.setEstoqueAtual(15);
        dto.setCategoriaId(categoriaId);

        String response = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/produtos/" + id))
                .andExpect(status().isNoContent());
    }


}
