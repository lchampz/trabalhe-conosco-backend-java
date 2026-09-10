package br.com.docket.cartorios.controller.web;

import br.com.docket.cartorios.client.CertidaoApiClient;
import br.com.docket.cartorios.dto.CertidaoRequest;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.shared.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CertidaoWebController.class)
class CertidaoWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertidaoApiClient certidaoApiClient;

    @Test
    void shouldRenderListWithCertidoesFromApi() throws Exception {
        when(certidaoApiClient.findAll(eq(null), eq(0))).thenReturn(page());

        mockMvc.perform(get("/certidoes"))
                .andExpect(status().isOk())
                .andExpect(view().name("certidao/lista"))
                .andExpect(model().attributeExists("certidoes"));
    }

    @Test
    void shouldRenderEmptyForm() throws Exception {
        mockMvc.perform(get("/certidoes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("certidao/formulario"))
                .andExpect(model().attributeExists("certidaoRequest"));
    }

    @Test
    void shouldRedirectAfterCreating() throws Exception {
        mockMvc.perform(post("/certidoes").param("nome", "Certidão de Nascimento"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/certidoes"))
                .andExpect(flash().attributeExists("mensagem"));

        verify(certidaoApiClient).create(any(CertidaoRequest.class));
    }

    @Test
    void shouldReturnToFormWhenNomeIsInvalid() throws Exception {
        mockMvc.perform(post("/certidoes").param("nome", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("certidao/formulario"))
                .andExpect(model().attributeHasFieldErrors("certidaoRequest", "nome"));

        verify(certidaoApiClient, never()).create(any(CertidaoRequest.class));
    }

    @Test
    void shouldRedirectWithErrorWhenApiReturnsNotFound() throws Exception {
        when(certidaoApiClient.findById(99L))
                .thenThrow(HttpClientErrorException.create(org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found", org.springframework.http.HttpHeaders.EMPTY, new byte[0], null));

        mockMvc.perform(get("/certidoes/99/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/certidoes"))
                .andExpect(flash().attributeExists("erro"));
    }

    private PageResponse<CertidaoResponse> page() {
        CertidaoResponse certidao = new CertidaoResponse(1L, "Certidão de Nascimento", Instant.parse("2026-01-01T12:00:00Z"));
        return new PageResponse<>(List.of(certidao), 0, 10, 1, 1, true, true);
    }
}
