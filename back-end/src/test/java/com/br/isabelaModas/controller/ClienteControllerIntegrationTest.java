package com.br.isabelaModas.controller;

import com.br.isabelaModas.NoSecurityConfig;
import com.br.isabelaModas.TestSecurityConfig;
import com.br.isabelaModas.dtos.EnderecoDto;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.br.isabelaModas.dtos.ClienteRequestDto;
import com.br.isabelaModas.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ClienteControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll(); // limpa antes de cada teste
    }

    private ClienteRequestDto criarClienteDto(String nome, String cpf, String email) {
        EnderecoDto endereco = new EnderecoDto();
        endereco.setCep("32600000");
        endereco.setLogradouro("Rua Teste");
        endereco.setComplemento("Casa");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Betim");
        endereco.setUf("MG");

        ClienteRequestDto dto = new ClienteRequestDto();
        dto.setNome(nome);
        dto.setCpf(cpf);
        dto.setEmail(email);
        dto.setTelefone("31999999999");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setEndereco(endereco);
        return dto;
    }

    @Test
    void deveCriarCliente() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Leandro", "39053344705", "leandro@gmail.com");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Leandro"))
                .andExpect(jsonPath("$.cpf").value("39053344705"))
                .andExpect(jsonPath("$.endereco.localidade").value("Betim"))
                .andExpect(jsonPath("$.endereco.uf").value("MG"));
    }

    @Test
    void deveListarClientes() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Maria", "11144477735", "maria@email.com");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Maria"));
    }

    @Test
    void deveBuscarClientePorCpf() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Carlos", "39053344705", "carlos@email.com");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes/cpf/39053344705"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    void deveBuscarClientePorEmail() throws Exception {
        ClienteRequestDto dto = criarClienteDto("João", "39053344705", "joao@email.com");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    void deveBuscarClientePorNomeParcial() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Ana Paula", "11144477735", "ana@email.com");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes/search?nome=Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana Paula"));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Carlos", "39053344705", "carlos@email.com");

        String response = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        dto.setNome("Carlos Atualizado");

        mockMvc.perform(put("/api/v1/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    void deveDeletarCliente() throws Exception {
        ClienteRequestDto dto = criarClienteDto("Ana", "39053344705", "ana@email.com");

        String response = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/clientes/" + id))
                .andExpect(status().isNoContent());
    }



}
