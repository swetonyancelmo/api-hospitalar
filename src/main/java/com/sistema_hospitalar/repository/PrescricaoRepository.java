package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescricaoRepository extends JpaRepository<Prescricao, String> {
}
