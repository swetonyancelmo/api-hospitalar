package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.FichaAtendimento;
import com.sistema_hospitalar.domain.enums.StatusAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FichaAtendimentoRepository extends JpaRepository<FichaAtendimento, String> {

    Optional<FichaAtendimento> findByPacienteIdAndAtivaTrue(String pacienteId);

    List<FichaAtendimento> findByStatusAndAtivaTrue(StatusAtendimento status);

    @Query("SELECT f FROM FichaAtendimento f JOIN f.triagem t WHERE f.status IN :statusList AND f.ativa = true ORDER BY t.prioridade ASC")
    List<FichaAtendimento> findByStatusInOrderByPrioridade(@Param("statusList") List<StatusAtendimento> statusList);
}
