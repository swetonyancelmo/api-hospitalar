package com.sistema_hospitalar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sistema_hospitalar.domain.Paciente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class PacienteDTO {

    public record Create(
            @NotBlank(message = "Nome não pode estar em branco.")
            String nomeCompleto,

            @NotBlank(message = "CPF não pode estar em branco.")
            @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
            String cpf,

            @NotNull(message = "Data de nascimento não pode ser nula")
            @Past(message = "Data de nascimento deve ser no passado")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataNascimento
    ){
        public Paciente toEntity() {
            return new Paciente(null, this.nomeCompleto, this.cpf, this.dataNascimento, null);
        }
    }

    public record Response(
            String id,
            String nomeCompleto,
            String cpf,
            LocalDate dataNascimento
    ) {
        public Response(Paciente paciente){
            this(paciente.getId(), paciente.getNomeCompleto(), paciente.getCpf(), paciente.getDataNascimento());
        }
    }
}
