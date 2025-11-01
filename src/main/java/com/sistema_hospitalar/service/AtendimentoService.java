package com.sistema_hospitalar.service;

import com.sistema_hospitalar.domain.FichaAtendimento;
import com.sistema_hospitalar.domain.Paciente;
import com.sistema_hospitalar.dto.FichaAtendimentoDTO;
import com.sistema_hospitalar.dto.IniciarAtendimentoRequestDTO;
import com.sistema_hospitalar.exception.BusinessRuleException;
import com.sistema_hospitalar.exception.ResourceNotFoundException;
import com.sistema_hospitalar.repository.FichaAtendimentoRepository;
import com.sistema_hospitalar.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtendimentoService {

    @Autowired
    private FichaAtendimentoRepository fichaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public FichaAtendimentoDTO iniciarAtendimento(IniciarAtendimentoRequestDTO dto){
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + dto.pacienteId()));

        fichaRepository.findByPacienteIdAndAtivaTrue(paciente.getId()).ifPresent(ficha -> {
            throw new BusinessRuleException("Paciente já possui uma ficha de atendimento ativa (ID: " + ficha.getId() + ")");
        });

        FichaAtendimento novaFicha = new FichaAtendimento(paciente);
        fichaRepository.save(novaFicha);

        return new FichaAtendimentoDTO(novaFicha);
    }
}
