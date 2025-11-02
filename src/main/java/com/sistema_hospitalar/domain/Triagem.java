package com.sistema_hospitalar.domain;

import com.sistema_hospitalar.domain.enums.PrioridadeRisco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "triagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "ficha_id", nullable = false)
    private FichaAtendimento ficha;

    @ManyToOne
    @JoinColumn(name = "enfermeiro_id", nullable = false)
    private Usuario enfermeiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeRisco prioridade;

    @CreationTimestamp
    private LocalDateTime dataHoraTriagem;
}
