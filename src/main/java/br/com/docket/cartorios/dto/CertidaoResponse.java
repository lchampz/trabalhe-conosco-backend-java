package br.com.docket.cartorios.dto;

import java.sql.Timestamp;
import java.time.Instant;

public record CertidaoResponse(Long id, String nome, Instant createdAt) {}