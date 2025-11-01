package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.FichaAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichaAtendimentoRepository extends JpaRepository<FichaAtendimento, String> {

    Optional<FichaAtendimento> findByPacienteIdAndAtivaTrue(String pacienteId);
}
