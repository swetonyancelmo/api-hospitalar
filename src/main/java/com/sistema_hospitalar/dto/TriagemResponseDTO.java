package com.sistema_hospitalar.dto;

import com.sistema_hospitalar.domain.Triagem;
import com.sistema_hospitalar.domain.enums.PrioridadeRisco;

import java.time.LocalDateTime;

public record TriagemResponseDTO(
        String triagemId,
        String fichaId,
        String pacienteNome,
        String enfermeiraNome,
        PrioridadeRisco prioridade,
        LocalDateTime dataHoraTriagem
) {
    public TriagemResponseDTO(Triagem triagem){
        this(
                triagem.getId(),
                triagem.getFicha().getId(),
                triagem.getFicha().getPaciente().getNomeCompleto(),
                triagem.getEnfermeiro().getUsername(),
                triagem.getPrioridade(),
                triagem.getDataHoraTriagem()
        );
    }
}
