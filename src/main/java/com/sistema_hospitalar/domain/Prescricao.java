package com.sistema_hospitalar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescricoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "atendimento_id", nullable = false)
    private AtendimentoMedico atendimentoMedico;

    @Column(nullable = false)
    private String medicamento;

    @Column(nullable = false)
    private String dosagem;

    @Column(nullable = false)
    private Boolean administrado = false;
}
