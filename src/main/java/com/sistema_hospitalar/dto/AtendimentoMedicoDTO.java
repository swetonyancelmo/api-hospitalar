package com.sistema_hospitalar.dto;

import com.sistema_hospitalar.domain.AtendimentoMedico;
import com.sistema_hospitalar.domain.FichaAtendimento;
import com.sistema_hospitalar.domain.Prescricao;
import com.sistema_hospitalar.domain.enums.CondutaMedica;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AtendimentoMedicoDTO {

    public record PrescricaoRequest(
            @NotBlank String medicamento,
            @NotBlank String dosagem
    ) {
    }

    public record Request(
            @NotBlank(message = "O parecer é obrigatório")
            String parecer,

            @NotNull(message = "A conduta é obrigatória")
            CondutaMedica conduta,

            @Valid
            @Size(min = 0)
            List<PrescricaoRequest> prescricoes
    ) {
    }

    public record PrescricaoResponse(
            String id,
            String medicamento,
            String dosagem,
            boolean administracao
    ) {
        public PrescricaoResponse(Prescricao p) {
            this(p.getId(), p.getMedicamento(), p.getDosagem(), p.getAdministrado());
        }
    }

    public record Response(
            String atendimentoId,
            String fichaId,
            String medicoNome,
            String parecer,
            CondutaMedica conduta,
            LocalDateTime dataHoraAtendimento,
            List<PrescricaoResponse> prescricoes
    ) {
        public Response(AtendimentoMedico atendimento) {
            this(
                    atendimento.getId(),
                    atendimento.getFicha().getId(),
                    atendimento.getMedico().getUsername(),
                    atendimento.getParecer(),
                    atendimento.getConduta(),
                    atendimento.getDataHoraAtendimento(),
                    atendimento.getPrescricoes() != null ?
                            atendimento.getPrescricoes().stream().map(PrescricaoResponse::new).collect(Collectors.toList()) :
                            List.of()
            );
        }
    }

    public record FilaMedico(
            String fichaId,
            String pacienteNome,
            String prioridade,
            LocalDateTime dataEntradaTriagem
    ) {
        public FilaMedico(FichaAtendimento ficha){
            this(
                    ficha.getId(),
                    ficha.getPaciente().getNomeCompleto(),
                    ficha.getTriagem().getPrioridade().name(),
                    ficha.getTriagem().getDataHoraTriagem()
            );
        }
    }

}
