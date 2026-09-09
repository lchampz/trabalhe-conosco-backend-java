package br.com.docket.cartorios.controller;

import br.com.docket.cartorios.dto.CartorioResponse;
import br.com.docket.cartorios.dto.CertidaoRequest;
import br.com.docket.cartorios.dto.CertidaoResponse;
import br.com.docket.cartorios.service.CertidaoService;
import br.com.docket.cartorios.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/certidao")
@Tag(name = "Certidão", description = "CRUD de certidões")
public class CertidaoController {
    private final CertidaoService certidaoService;

    @GetMapping
    @Operation(summary = "Listagem de certidões")
    public PageResponse<CertidaoResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nome
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nome"));
        return PageResponse.of(certidaoService.list(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes de uma certidão")
    public CertidaoResponse findById(@PathVariable Long id) {
        return certidaoService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Criação de uma certidão")
    public ResponseEntity<CertidaoResponse> create(@Valid @RequestBody CertidaoRequest request) {
        CertidaoResponse certidao = certidaoService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(certidao.id()).toUri();
        return ResponseEntity.created(location).body(certidao);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma certidão")
    public ResponseEntity<CertidaoResponse> update(@PathVariable Long id, @Valid @RequestBody CertidaoRequest request) {
        CertidaoResponse certidao = certidaoService.update(id, request);
        return ResponseEntity.ok(certidao);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleção de uma certidão")
    public ResponseEntity<CertidaoResponse> delete(@PathVariable Long id) {
        certidaoService.delete(id);
        return ResponseEntity.ok().build();
    }

}
