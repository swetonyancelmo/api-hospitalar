package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.Triagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriagemRepository extends JpaRepository<Triagem, String> {
}
