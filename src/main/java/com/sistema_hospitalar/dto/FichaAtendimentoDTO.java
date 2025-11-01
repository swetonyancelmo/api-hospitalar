package com.sistema_hospitalar.dto;

import com.sistema_hospitalar.domain.FichaAtendimento;
import com.sistema_hospitalar.domain.enums.StatusAtendimento;

import java.time.LocalDateTime;

public record FichaAtendimentoDTO(
        String id,
        String pacienteId,
        String nomePaciente,
        StatusAtendimento status,
        LocalDateTime dataHoraEntrada
) {
    public FichaAtendimentoDTO(FichaAtendimento ficha){
        this(
                ficha.getId(),
                ficha.getPaciente().getId(),
                ficha.getPaciente().getNomeCompleto(),
                ficha.getStatus(),
                ficha.getDataHoraEntrada()
        );
    }
}
