package com.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotBlank;

public record IniciarAtendimentoRequestDTO(
        @NotBlank(message = "ID do paciente é obrigatório")
        String pacienteId
) {}
