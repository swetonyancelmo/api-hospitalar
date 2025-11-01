package com.sistema_hospitalar.domain;

import com.sistema_hospitalar.domain.enums.StatusAtendimento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichas_atendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtendimento status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataHoraEntrada;

    private LocalDateTime dataHoraSaida;

    @Column(nullable = false)
    private Boolean ativa;

    public FichaAtendimento(Paciente paciente){
        this.paciente = paciente;
        this.status = StatusAtendimento.AGUARDANDO_TRIAGEM;
        this.ativa = true;
    }
}
