package br.com.docket.cartorios.controller;

import br.com.docket.cartorios.dto.CartorioRequest;
import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.dto.CertidaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import br.com.docket.cartorios.service.CartorioService;
import br.com.docket.cartorios.shared.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/cartorio")
public class CartorioController {

    private final CartorioService cartorioService;

    @GetMapping
    @Operation(summary = "Listagem de cartorio")
    public PageResponse<CartorioResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nome
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome"));
        return PageResponse.of(cartorioService.list(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca cartorio por id")
    public CartorioResponse findById(@PathVariable Long id) {
        return cartorioService.findById(id);
    }

    @GetMapping("/{id}/certidoes")
    @Operation(summary = "Listar certidoes emitidas pelo cartório")
    public List<CertidaoResponse> listCertidoes(@PathVariable Long id) {
        return cartorioService.listCertidoes(id);
    }

    @PostMapping
    @Operation(summary = "Criar cartório")
    public ResponseEntity<CartorioResponse> create(@Valid @RequestBody CartorioRequest request) {
        CartorioResponse cartorio = cartorioService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cartorio.id()).toUri();
        return ResponseEntity.created(location).body(cartorio);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cartório")
    public ResponseEntity<CartorioResponse> update(@PathVariable Long id, @Valid @RequestBody CartorioRequest request) {
        CartorioResponse cartorio = cartorioService.update(id, request);
        return ResponseEntity.ok(cartorio);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cartório")
    public ResponseEntity<CartorioResponse> delete(@PathVariable Long id) {
        cartorioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cartorioId}/certidoes/{certidaoId}")
    public ResponseEntity<Void> linkarCertidao(
            @PathVariable Long cartorioId,
            @PathVariable Long certidaoId) {
        cartorioService.linkCertidao(cartorioId, certidaoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartorioId}/certidoes/{certidaoId}")
    public ResponseEntity<Void> unlinkCertidao(
            @PathVariable Long cartorioId,
            @PathVariable Long certidaoId) {
        cartorioService.unlinkCertidao(cartorioId, certidaoId);
        return ResponseEntity.noContent().build();
    }


}
