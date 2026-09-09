package br.com.docket.cartorios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CertidaoRequest(
        @NotBlank(message = "Informe o nome da certidão")
        @Size(max = 150, min = 3 ,message = "de 3 a 150 caracteres")
        String nome
) { }
