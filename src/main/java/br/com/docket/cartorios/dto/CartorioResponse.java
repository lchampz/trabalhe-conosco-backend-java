package br.com.docket.cartorios.dto;

import java.time.Instant;
import java.util.List;

public record CartorioResponse(
        Long id,
        String nome,
        String cep,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        List<CertidaoResponse> certidoes,
        int totalCertidoes,
        Instant createdAt) {}