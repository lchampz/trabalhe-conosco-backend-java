package br.com.docket.cartorios;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CertidaoRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartorioApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRunFullCrudFlow() throws Exception {
        Long certidaoId = createCertidao("Certidão de Integração");

        String criado = mockMvc.perform(post("/api/v1/cartorio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartorioRequest("Cartório de Integração", Set.of(certidaoId)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCertidoes").value(1))
                .andReturn().getResponse().getContentAsString();

        Long cartorioId = objectMapper.readTree(criado).get("id").asLong();

        mockMvc.perform(get("/api/v1/cartorio/{id}", cartorioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cartório de Integração"))
                .andExpect(jsonPath("$.certidoes[0].nome").value("Certidão de Integração"));

        mockMvc.perform(put("/api/v1/cartorio/{id}", cartorioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartorioRequest("Cartório Renomeado", Set.of(certidaoId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cartório Renomeado"));

        mockMvc.perform(delete("/api/v1/cartorio/{id}", cartorioId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/cartorio/{id}", cartorioId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectCartorioWithUnknownCertidao() throws Exception {
        mockMvc.perform(post("/api/v1/cartorio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartorioRequest("Cartório Inválido", Set.of(9999L)))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Certidão 9999 não encontrado"));
    }

    @Test
    void shouldRejectDuplicatedCertidaoName() throws Exception {
        createCertidao("Certidão Duplicada");

        mockMvc.perform(post("/api/v1/certidao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CertidaoRequest("certidão duplicada"))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldListCertidoesOfCartorio() throws Exception {
        Long certidaoId = createCertidao("Certidão Vinculada");
        String criado = mockMvc.perform(post("/api/v1/cartorio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartorioRequest("Cartório Vinculado", Set.of(certidaoId)))))
                .andReturn().getResponse().getContentAsString();

        Long cartorioId = objectMapper.readTree(criado).get("id").asLong();

        mockMvc.perform(get("/api/v1/cartorio/{id}/certidoes", cartorioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Certidão Vinculada"));
    }

    @Test
    void shouldPaginateCertidoes() throws Exception {
        String corpo = mockMvc.perform(get("/api/v1/certidao").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(corpo);

        assertThat(json.get("content")).hasSizeLessThanOrEqualTo(2);
        assertThat(json.get("size").asInt()).isEqualTo(2);
    }

    private Long createCertidao(String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/v1/certidao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CertidaoRequest(nome))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private CartorioRequest cartorioRequest(String nome, Set<Long> certidoes) {
        return new CartorioRequest(nome, "01302000", "Rua da Consolação", "100",
                null, "Consolação", "São Paulo", "SP", certidoes);
    }
}
