package com.br.isabelaModas.controller;

import com.br.isabelaModas.TestSecurityConfig;
import com.br.isabelaModas.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.br.isabelaModas.entity.Categoria;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.repository.CategoriaRepository;
import com.br.isabelaModas.repository.CompraRepository;
import com.br.isabelaModas.repository.FornecedorRepository;
import com.br.isabelaModas.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CompraControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Long fornecedorId;
    private Long produtoId;

    @BeforeEach
    void setUp() throws Exception {
        compraRepository.deleteAll();
        fornecedorRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria categoria = new Categoria();
        categoria.setNome("Roupas");
        categoria.setDescricao("Categoria de roupas");
        categoriaRepository.save(categoria);

        Produto produto = new Produto();
        produto.setNome("Camisa");
        produto.setPreco(new BigDecimal("25.00"));
        produto.setTamanho("M");
        produto.setCor("Azul");
        produto.setEstoqueAtual(5);
        produto.setCategoria(categoria);
        produtoId = produtoRepository.save(produto).getId();

        FornecedorRequestDto fornecedorDto = new FornecedorRequestDto();
        fornecedorDto.setNome("Fornecedor Teste");
        fornecedorDto.setCnpj("27865757000102");
        fornecedorDto.setEmail("fornecedor@email.com");
        fornecedorDto.setTelefone("31999999999");
        fornecedorDto.setEndereco(new EnderecoDto("32604123", "Rua A", "Apto 1", "Centro", "Betim", "MG"));

        MvcResult fornecedorResult = mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fornecedorDto)))
                .andExpect(status().isCreated())
                .andReturn();

        FornecedorResponseDto fornecedorResponse = objectMapper.readValue(
                fornecedorResult.getResponse().getContentAsString(),
                FornecedorResponseDto.class);
        fornecedorId = fornecedorResponse.getId();
    }

    @AfterEach
    void tearDown() {
        compraRepository.deleteAll();
        fornecedorRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    private CompraRequestDto criarRequestDto() {
        ItemCompraDto item1 = new ItemCompraDto();
        item1.setProdutoId(produtoId);
        item1.setQuantidade(2);
        item1.setPrecoUnitario(new BigDecimal("10.00"));

        ItemCompraDto item2 = new ItemCompraDto();
        item2.setProdutoId(produtoId);
        item2.setQuantidade(1);
        item2.setPrecoUnitario(new BigDecimal("20.00"));

        CompraRequestDto dto = new CompraRequestDto();
        dto.setDataCompra(LocalDate.now());
        dto.setFornecedorId(fornecedorId);
        dto.setItens(List.of(item1, item2));
        return dto;
    }

    @Test
    void deveCriarCompraComSucesso() throws Exception {
        mockMvc.perform(post("/api/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(40.00));
    }

    @Test
    void deveIncrementarEstoqueAoCriarCompra() throws Exception {
        int estoqueAntes = produtoRepository.findById(produtoId).orElseThrow().getEstoqueAtual();

        mockMvc.perform(post("/api/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequestDto())))
                .andExpect(status().isCreated());

        int estoqueDepois = produtoRepository.findById(produtoId).orElseThrow().getEstoqueAtual();
        org.junit.jupiter.api.Assertions.assertEquals(estoqueAntes + 3, estoqueDepois);
    }

    @Test
    void deveListarCompras() throws Exception {
        mockMvc.perform(post("/api/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequestDto())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valorTotal").value(40.00));
    }

    @Test
    void deveAtualizarCompra() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequestDto())))
                .andReturn();

        CompraResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CompraResponseDto.class);

        CompraRequestDto atualizado = criarRequestDto();
        atualizado.setDataCompra(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/api/v1/compras/{id}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotal").value(40.00));
    }

    @Test
    void deveDeletarCompra() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequestDto())))
                .andReturn();

        CompraResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CompraResponseDto.class);

        mockMvc.perform(delete("/api/v1/compras/{id}", response.getId()))
                .andExpect(status().isNoContent());
    }
}
