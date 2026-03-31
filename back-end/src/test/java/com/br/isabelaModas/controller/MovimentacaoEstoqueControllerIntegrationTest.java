package com.br.isabelaModas.controller;

import com.br.isabelaModas.NoSecurityConfig;
import com.br.isabelaModas.TestSecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.br.isabelaModas.dtos.MovimentacaoEstoqueRequestDto;
import com.br.isabelaModas.entity.Cliente;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.repository.ClienteRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class MovimentacaoEstoqueControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRegistrarSaidaTemporaria() throws Exception {
        // Criando produto
        Produto produto = new Produto();
        produto.setNome("Camisa");
        produto.setPreco(BigDecimal.TEN);
        produto.setTamanho("M");
        produto.setCor("Azul");
        produto.setEstoqueAtual(10);
        produtoRepository.save(produto);

        // Criando cliente
        Cliente cliente = new Cliente();
        cliente.setNome("Leandro");
        cliente.setCpf("12345678900");
        cliente.setEmail("leandro@email.com");
        cliente.setTelefone("31999999999");
        clienteRepository.save(cliente);

        // DTO da requisição
        MovimentacaoEstoqueRequestDto dto = new MovimentacaoEstoqueRequestDto();
        dto.setProdutoId(produto.getId());
        dto.setClienteId(cliente.getId());
        dto.setQuantidade(3);

        mockMvc.perform(post("/api/v1/movimentacoes-estoque/saida-temporaria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimentacao").value("SAIDA_TEMPORARIA"));
    }

    @Test
    void deveRegistrarDevolucao() throws Exception {
        // Criando produto
        Produto produto = new Produto();
        produto.setNome("Camisa");
        produto.setPreco(BigDecimal.TEN);
        produto.setTamanho("M");
        produto.setCor("Azul");
        produto.setEstoqueAtual(5);
        produtoRepository.save(produto);

        // Criando cliente
        Cliente cliente = new Cliente();
        cliente.setNome("Leandro");
        cliente.setCpf("12345678900");
        cliente.setEmail("leandro@email.com");
        cliente.setTelefone("31999999999");
        clienteRepository.save(cliente);

        // DTO da requisição
        MovimentacaoEstoqueRequestDto dto = new MovimentacaoEstoqueRequestDto();
        dto.setProdutoId(produto.getId());
        dto.setClienteId(cliente.getId());
        dto.setQuantidade(2);

        mockMvc.perform(post("/api/v1/movimentacoes-estoque/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimentacao").value("DEVOLUCAO"));
    }


}
