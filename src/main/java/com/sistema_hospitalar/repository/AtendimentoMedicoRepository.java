package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.AtendimentoMedico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoMedicoRepository extends JpaRepository<AtendimentoMedico, String> {
}
