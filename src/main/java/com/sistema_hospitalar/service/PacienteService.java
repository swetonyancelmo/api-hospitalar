package com.sistema_hospitalar.service;

import com.sistema_hospitalar.domain.Paciente;
import com.sistema_hospitalar.dto.PacienteDTO;
import com.sistema_hospitalar.exception.BusinessRuleException;
import com.sistema_hospitalar.exception.ResourceNotFoundException;
import com.sistema_hospitalar.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public PacienteDTO.Response buscarPorCpf(String cpf){
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com CPF: " + cpf));
        return new PacienteDTO.Response(paciente);
    }

    @Transactional
    public PacienteDTO.Response criarPaciente(PacienteDTO.Create dto){
        pacienteRepository.findByCpf(dto.cpf()).ifPresent(paciente -> {
            throw new BusinessRuleException("CPF já cadastrado no sistema");
        });

        Paciente novoPaciente = dto.toEntity();
        pacienteRepository.save(novoPaciente);

        return new PacienteDTO.Response(novoPaciente);
    }
}
