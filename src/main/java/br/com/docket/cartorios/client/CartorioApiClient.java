package br.com.docket.cartorios.client;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.shared.PageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class CartorioApiClient {

    private final RestClient restClient;

    public CartorioApiClient(RestClient.Builder builder, @Value("${app.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl + "/api/v1/cartorio").build();
    }

    public PageResponse<CartorioResponse> findAll(String nome, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", page)
                        .queryParamIfPresent("nome", Optional.ofNullable(nome))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<CartorioResponse>>() {});
    }

    public CartorioResponse findById(Long id) {
        return restClient.get().uri("/{id}", id).retrieve().body(CartorioResponse.class);
    }

    public List<CertidaoResponse> listCertidoes(Long id) {
        return restClient.get().uri("/{id}/certidoes", id).retrieve()
                .body(new ParameterizedTypeReference<List<CertidaoResponse>>() {});
    }

    public CartorioResponse create(CartorioRequest request) {
        return restClient.post().body(request).retrieve().body(CartorioResponse.class);
    }

    public CartorioResponse update(Long id, CartorioRequest request) {
        return restClient.put().uri("/{id}", id).body(request).retrieve().body(CartorioResponse.class);
    }

    public void delete(Long id) {
        restClient.delete().uri("/{id}", id).retrieve().toBodilessEntity();
    }
}
