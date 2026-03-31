package com.br.isabelaModas.controller;

import com.br.isabelaModas.NoSecurityConfig;
import com.br.isabelaModas.TestSecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)

public class EmpresaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarEmpresa() throws Exception {
        String json = """
            {
              "nome": "Isabela Modas",
              "cnpj": "12345678000195",
              "email": "contato@isabelamodas.com",
              "telefone": "31999999999",
              "endereco": { "cep": "32600-000" },
              "numero": 100
            }
        """;

        mockMvc.perform(post("/api/v1/empresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Isabela Modas"))
                .andExpect(jsonPath("$.endereco.cep").value("32600-000"));
    }

    @Test
    void deveBuscarEmpresa() throws Exception {
        mockMvc.perform(get("/api/v1/empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Isabela Modas"));
    }


}
