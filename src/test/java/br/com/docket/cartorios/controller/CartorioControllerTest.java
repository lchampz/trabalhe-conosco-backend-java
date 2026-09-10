package br.com.docket.cartorios.controller;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.service.CartorioService;
import br.com.docket.cartorios.shared.error.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartorioController.class)
class CartorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartorioService cartorioService;

    @Test
    void shouldReturnCartorioById() throws Exception {
        when(cartorioService.findById(1L)).thenReturn(response());

        mockMvc.perform(get("/api/v1/cartorio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("1º Ofício de Registro Civil"))
                .andExpect(jsonPath("$.totalCertidoes").value(0));
    }

    @Test
    void shouldReturnPagedList() throws Exception {
        Page<CartorioResponse> page = new PageImpl<>(List.of(response()), PageRequest.of(0, 20), 1);
        when(cartorioService.list(eq(null), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/cartorio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("1º Ofício de Registro Civil"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void shouldReturnProblemDetailWhenNotFound() throws Exception {
        when(cartorioService.findById(99L)).thenThrow(new ResourceNotFoundException("Cartório", 99L));

        mockMvc.perform(get("/api/v1/cartorio/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Cartório 99 não encontrado"));
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        CartorioRequest invalido = new CartorioRequest("", "123", "", "", null, null, "", "sp", Set.of());

        mockMvc.perform(post("/api/v1/cartorio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldCreateCartorio() throws Exception {
        CartorioRequest request = new CartorioRequest("1º Ofício de Registro Civil", "01302000", "Rua da Consolação",
                "100", null, "Consolação", "São Paulo", "SP", Set.of(1L));
        when(cartorioService.create(any(CartorioRequest.class))).thenReturn(response());

        mockMvc.perform(post("/api/v1/cartorio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    private CartorioResponse response() {
        return new CartorioResponse(1L, "1º Ofício de Registro Civil", "01302000", "Rua da Consolação", "100",
                null, "Consolação", "São Paulo", "SP", List.of(), 0, Instant.parse("2026-01-01T12:00:00Z"));
    }
}
