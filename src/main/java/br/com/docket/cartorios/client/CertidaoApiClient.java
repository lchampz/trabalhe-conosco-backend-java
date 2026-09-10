package br.com.docket.cartorios.client;

import br.com.docket.cartorios.dto.CertidaoRequest;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.shared.PageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class CertidaoApiClient {

    private final RestClient restClient;

    public CertidaoApiClient(RestClient.Builder builder, @Value("${app.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl + "/api/v1/certidao").build();
    }

    public PageResponse<CertidaoResponse> findAll(String nome, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", page)
                        .queryParamIfPresent("nome", Optional.ofNullable(nome))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<CertidaoResponse>>() {});
    }

    public PageResponse<CertidaoResponse> listAll() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("size", 1000).build())
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<CertidaoResponse>>() {});
    }

    public CertidaoResponse findById(Long id) {
        return restClient.get().uri("/{id}", id).retrieve().body(CertidaoResponse.class);
    }

    public CertidaoResponse create(CertidaoRequest request) {
        return restClient.post().body(request).retrieve().body(CertidaoResponse.class);
    }

    public CertidaoResponse update(Long id, CertidaoRequest request) {
        return restClient.put().uri("/{id}", id).body(request).retrieve().body(CertidaoResponse.class);
    }

    public void delete(Long id) {
        restClient.delete().uri("/{id}", id).retrieve().toBodilessEntity();
    }
}
