package com.sistema_hospitalar.domain;

import com.sistema_hospitalar.domain.enums.CondutaMedica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "atendimentos_medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "ficha_id", nullable = false)
    private FichaAtendimento ficha;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;

    @Column(columnDefinition = "TEXT")
    private String parecer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondutaMedica conduta;

    @CreationTimestamp
    private LocalDateTime dataHoraAtendimento;

    @OneToMany(mappedBy = "atendimentoMedico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Prescricao> prescricoes;
}
