package com.sistema_hospitalar.dto;

import com.sistema_hospitalar.domain.enums.PrioridadeRisco;
import jakarta.validation.constraints.NotNull;

public record TriagemResquestDTO(
        @NotNull(message = "A prioridade de risco é obrigatória")
        PrioridadeRisco prioridade
) {
}
