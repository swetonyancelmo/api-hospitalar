package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescricaoRepository extends JpaRepository<Prescricao, String> {

    List<Prescricao> findByAtendimentoMedicoIdAndAdministradoFalse(String atendimentoMedicoId);
}
