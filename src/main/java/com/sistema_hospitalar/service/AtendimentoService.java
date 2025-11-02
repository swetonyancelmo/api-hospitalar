package com.sistema_hospitalar.service;

import com.sistema_hospitalar.domain.*;
import com.sistema_hospitalar.domain.enums.StatusAtendimento;
import com.sistema_hospitalar.dto.*;
import com.sistema_hospitalar.exception.BusinessRuleException;
import com.sistema_hospitalar.exception.ResourceNotFoundException;
import com.sistema_hospitalar.repository.AtendimentoMedicoRepository;
import com.sistema_hospitalar.repository.FichaAtendimentoRepository;
import com.sistema_hospitalar.repository.PacienteRepository;
import com.sistema_hospitalar.repository.TriagemRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    @Autowired
    private FichaAtendimentoRepository fichaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TriagemRepository triagemRepository;

    @Autowired
    private AtendimentoMedicoRepository atendimentoMedicoRepository;

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

    @Transactional(readOnly = true)
    public List<FichaAtendimentoDTO> listarAguardandoTriagem() {
        List<FichaAtendimento> fichas = fichaRepository
                .findByStatusAndAtivaTrue(StatusAtendimento.AGUARDANDO_TRIAGEM);

        return fichas.stream()
                .map(FichaAtendimentoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TriagemResponseDTO registrarTriagem(String fichaId, TriagemResquestDTO dto){
        Usuario enfermeiro = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FichaAtendimento ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada: " + fichaId));

        if(ficha.getStatus() != StatusAtendimento.AGUARDANDO_TRIAGEM) {
            throw new BusinessRuleException("Esta ficha não está aguardando triagem. Status atual: " + ficha.getStatus());
        }

        Triagem novaTriagem = new Triagem();
        novaTriagem.setFicha(ficha);
        novaTriagem.setEnfermeiro(enfermeiro);
        novaTriagem.setPrioridade(dto.prioridade());

        ficha.setStatus(StatusAtendimento.AGUARDANDO_MEDICO);
        ficha.setTriagem(novaTriagem);

        triagemRepository.save(novaTriagem);

        return new TriagemResponseDTO(novaTriagem);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoMedicoDTO.FilaMedico> listarAguardandoMedico(){
        List<FichaAtendimento> fichas = fichaRepository
                .findByStatusOrderByPrioridade(StatusAtendimento.AGUARDANDO_MEDICO);

        return fichas.stream()
                .map(AtendimentoMedicoDTO.FilaMedico::new)
                .collect(Collectors.toList());
    }

    public AtendimentoMedicoDTO.Response registrarAvaliacaoMedica(String fichaId, AtendimentoMedicoDTO.Request dto){
        Usuario medico= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FichaAtendimento ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada: " + fichaId));

        if(ficha.getStatus() != StatusAtendimento.AGUARDANDO_MEDICO){
            throw new BusinessRuleException("Esta ficha não está aguardando atendimento médico. Status atual: " + ficha.getStatus());
        }

        AtendimentoMedico atendimento = new AtendimentoMedico();
        atendimento.setFicha(ficha);
        atendimento.setMedico(medico);
        atendimento.setParecer(dto.parecer());
        atendimento.setConduta(dto.conduta());

        switch (dto.conduta()) {
            case MEDICACAO:
                if(dto.prescricoes() == null || dto.prescricoes().isEmpty()){
                    throw new BusinessRuleException("Conduta de MEDICAÇÃO exige ao menos uma prescrição");
                }

                List<Prescricao> prescricoes = dto.prescricoes().stream().map(pDto -> new Prescricao(null,
                        atendimento, pDto.medicamento(), pDto.dosagem(), false)
                ).collect(Collectors.toList());

                atendimento.setPrescricoes(prescricoes);
                ficha.setStatus(StatusAtendimento.EM_MEDICACAO);
                break;

            case ALTA:
            case ENCAMINHAMENTO:
                ficha.setStatus(StatusAtendimento.ALTA);
                break;
        }

        atendimentoMedicoRepository.save(atendimento);
        fichaRepository.save(ficha);

        return new AtendimentoMedicoDTO.Response(atendimento);
    }
}
