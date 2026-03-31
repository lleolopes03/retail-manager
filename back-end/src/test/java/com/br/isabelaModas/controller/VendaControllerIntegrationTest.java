package com.br.isabelaModas.controller;

import com.br.isabelaModas.TestSecurityConfig;
import com.br.isabelaModas.dtos.ItemVendaDto;
import com.br.isabelaModas.dtos.VendaRequestDto;
import com.br.isabelaModas.entity.Categoria;
import com.br.isabelaModas.entity.Cliente;
import com.br.isabelaModas.entity.Endereco;
import com.br.isabelaModas.entity.Produto;
import com.br.isabelaModas.entity.enums.StatusPagamento;
import com.br.isabelaModas.repository.CategoriaRepository;
import com.br.isabelaModas.repository.ClienteRepository;
import com.br.isabelaModas.repository.MovimentacaoEstoqueRepository;
import com.br.isabelaModas.repository.ParcelaRepository;
import com.br.isabelaModas.repository.ProdutoRepository;
import com.br.isabelaModas.repository.VendaRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class VendaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    private Long categoriaId;
    private Long produtoId;
    private Long clienteId;

    @BeforeEach
    void setUp() {
        parcelaRepository.deleteAll();
        vendaRepository.deleteAll();
        movimentacaoEstoqueRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
        clienteRepository.deleteAll();

        Categoria categoria = new Categoria();
        categoria.setNome("Roupas");
        categoria.setDescricao("Categoria de roupas");
        categoriaId = categoriaRepository.save(categoria).getId();

        Produto produto = new Produto();
        produto.setNome("Camisa Floral");
        produto.setPreco(new BigDecimal("50.00"));
        produto.setTamanho("M");
        produto.setCor("Azul");
        produto.setEstoqueAtual(20);
        produto.setCategoria(categoria);
        produtoId = produtoRepository.save(produto).getId();

        Endereco endereco = new Endereco();
        endereco.setCep("32600000");
        endereco.setLogradouro("Rua das Flores");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Betim");
        endereco.setUf("MG");

        Cliente cliente = new Cliente();
        cliente.setNome("Ana Paula");
        cliente.setCpf("12345678901");
        cliente.setEmail("ana@email.com");
        cliente.setTelefone("31999999999");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEndereco(endereco);
        clienteId = clienteRepository.save(cliente).getId();
    }

    @AfterEach
    void tearDown() {
        parcelaRepository.deleteAll();
        vendaRepository.deleteAll();
        movimentacaoEstoqueRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    private VendaRequestDto criarRequestDto(String tipoPagamento, int numeroParcelas) {
        ItemVendaDto item = new ItemVendaDto();
        item.setProdutoId(produtoId);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));

        VendaRequestDto dto = new VendaRequestDto();
        dto.setClienteId(clienteId);
        dto.setDataVenda(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(30));
        dto.setStatusPagamento("PENDENTE");
        dto.setTipoPagamento(tipoPagamento);
        dto.setNumeroParcelas(numeroParcelas);
        dto.setItens(List.of(item));
        return dto;
    }

    @Test
    void deveCriarVenda() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(100.0))
                .andExpect(jsonPath("$.statusPagamento").value("PENDENTE"))
                .andExpect(jsonPath("$.tipoPagamento").value("DINHEIRO"));
    }

    @Test
    void deveDarBaixaNoEstoqueAoCriarVenda() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Produto produtoAtualizado = produtoRepository.findById(produtoId).orElseThrow();
        assertEquals(18, produtoAtualizado.getEstoqueAtual());
    }

    @Test
    void deveRetornar400QuandoEstoqueInsuficiente() throws Exception {
        ItemVendaDto item = new ItemVendaDto();
        item.setProdutoId(produtoId);
        item.setQuantidade(100);
        item.setPrecoUnitario(new BigDecimal("50.00"));

        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);
        dto.setItens(List.of(item));

        mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarVendaPorId() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        String resposta = mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long vendaId = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(get("/api/v1/vendas/{id}", vendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vendaId))
                .andExpect(jsonPath("$.valorTotal").value(100.0));
    }

    @Test
    void deveRetornar400ParaVendaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/vendas/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarVendasPaginadas() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);
        mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/vendas")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void deveDeletarVenda() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        String resposta = mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long vendaId = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(delete("/api/v1/vendas/{id}", vendaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/vendas/{id}", vendaId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDevolverEstoqueAoDeletarVenda() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        String resposta = mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long vendaId = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(delete("/api/v1/vendas/{id}", vendaId))
                .andExpect(status().isNoContent());

        Produto produtoAtualizado = produtoRepository.findById(produtoId).orElseThrow();
        assertEquals(20, produtoAtualizado.getEstoqueAtual());
    }

    @Test
    void deveCriarVendaCarneEGerarParcelas() throws Exception {
        VendaRequestDto dto = criarRequestDto("CARNE", 3);
        dto.setPrimeiraParcela(LocalDate.now().plusDays(30));
        dto.setIntervaloDias(30);

        String resposta = mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long vendaId = objectMapper.readTree(resposta).get("id").asLong();

        assertEquals(3, parcelaRepository.findByVendaId(vendaId).size());
    }

    @Test
    void deveAtualizarStatusDaVenda() throws Exception {
        VendaRequestDto dto = criarRequestDto("DINHEIRO", 1);

        String resposta = mockMvc.perform(post("/api/v1/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long vendaId = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(patch("/api/v1/vendas/{vendaId}/status", vendaId)
                        .param("novoStatus", "PAGO"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/vendas/{id}", vendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusPagamento").value("PAGO"));
    }
}
