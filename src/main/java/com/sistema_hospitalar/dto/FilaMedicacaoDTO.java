package com.sistema_hospitalar.dto;


import com.sistema_hospitalar.domain.FichaAtendimento;
import com.sistema_hospitalar.domain.Prescricao;

import java.util.List;
import java.util.stream.Collectors;

record PrescricaoPendenteDTO(
        String prescricaoId,
        String medicamento,
        String dosagem
) {
    public PrescricaoPendenteDTO(Prescricao p) {
        this(p.getId(), p.getMedicamento(), p.getDosagem());
    }
}

public record FilaMedicacaoDTO(
        String fichaId,
        String pacienteNome,
        String medicoSolicitante,
        List<PrescricaoPendenteDTO> prescricoesPendentes
) {
    public FilaMedicacaoDTO(FichaAtendimento ficha) {
        this(
                ficha.getId(),
                ficha.getPaciente().getNomeCompleto(),
                ficha.getAtendimentoMedicos().get(ficha.getAtendimentoMedicos().size() - 1).getMedico().getUsername(),
                ficha.getAtendimentoMedicos().get(ficha.getAtendimentoMedicos().size() - 1).getPrescricoes().stream()
                        .filter(p -> !p.getAdministrado())
                        .map(PrescricaoPendenteDTO::new)
                        .collect(Collectors.toList())
        );
    }
}
