package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, String> {

    Optional<Paciente> findByCpf(String cpf);
}
