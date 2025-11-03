package com.sistema_hospitalar.service;

import com.sistema_hospitalar.domain.*;
import com.sistema_hospitalar.domain.enums.StatusAtendimento;
import com.sistema_hospitalar.dto.*;
import com.sistema_hospitalar.exception.BusinessRuleException;
import com.sistema_hospitalar.exception.ResourceNotFoundException;
import com.sistema_hospitalar.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Autowired
    private PrescricaoRepository prescricaoRepository;

    @Transactional
    public FichaAtendimentoDTO iniciarAtendimento(IniciarAtendimentoRequestDTO dto) {
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
    public TriagemResponseDTO registrarTriagem(String fichaId, TriagemResquestDTO dto) {
        Usuario enfermeiro = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FichaAtendimento ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada: " + fichaId));

        if (ficha.getStatus() != StatusAtendimento.AGUARDANDO_TRIAGEM) {
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
    public List<AtendimentoMedicoDTO.FilaMedico> listarAguardandoMedico() {
        List<StatusAtendimento> statusParaMedico = Arrays.asList(
                StatusAtendimento.AGUARDANDO_MEDICO,
                StatusAtendimento.AGUARDANDO_REAVALIACAO
        );

        List<FichaAtendimento> fichas = fichaRepository
                .findByStatusInOrderByPrioridade(statusParaMedico);

        return fichas.stream()
                .map(AtendimentoMedicoDTO.FilaMedico::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AtendimentoMedicoDTO.Response registrarAvaliacaoMedica(String fichaId, AtendimentoMedicoDTO.Request dto) {
        Usuario medico = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FichaAtendimento ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada: " + fichaId));

        if (ficha.getStatus() != StatusAtendimento.AGUARDANDO_MEDICO &&
                ficha.getStatus() != StatusAtendimento.AGUARDANDO_REAVALIACAO) {
            throw new BusinessRuleException("Esta ficha não está aguardando atendimento médico. Status atual: " + ficha.getStatus());
        }

        AtendimentoMedico atendimento = new AtendimentoMedico();
        atendimento.setFicha(ficha);
        atendimento.setMedico(medico);
        atendimento.setParecer(dto.parecer());
        atendimento.setConduta(dto.conduta());

        switch (dto.conduta()) {
            case MEDICACAO:
                if (dto.prescricoes() == null || dto.prescricoes().isEmpty()) {
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
                this.finalizarAtendimento(fichaId, medico.getId());
                break;
        }

        atendimentoMedicoRepository.save(atendimento);

        return new AtendimentoMedicoDTO.Response(atendimento);
    }

    @Transactional(readOnly = true)
    public List<FilaMedicacaoDTO> listarAguardandoMedicacao() {
        List<FichaAtendimento> fichas = fichaRepository
                .findByStatusAndAtivaTrue(StatusAtendimento.EM_MEDICACAO);

        return fichas.stream()
                .filter(f -> f.getAtendimentoMedicos() != null && !f.getAtendimentoMedicos().isEmpty())
                .map(FilaMedicacaoDTO::new)
                .filter(dto -> !dto.prescricoesPendentes().isEmpty()) // Segurança extra
                .collect(Collectors.toList());
    }

    @Transactional
    public void administrarMedicacao(String prescricaoId){
        Prescricao prescricao = prescricaoRepository.findById(prescricaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada: " + prescricaoId));

        if(prescricao.getAdministrado()){
            throw new BusinessRuleException("Este medicamento já foi administrado.");
        }

        prescricao.setAdministrado(true);
        prescricaoRepository.save(prescricao);

        String atendimentoId = prescricao.getAtendimentoMedico().getId();
        List<Prescricao> pendentes = prescricaoRepository
                .findByAtendimentoMedicoIdAndAdministradoFalse(atendimentoId);

        if (pendentes.isEmpty()) {
            FichaAtendimento ficha = prescricao.getAtendimentoMedico().getFicha();
            ficha.setStatus(StatusAtendimento.AGUARDANDO_REAVALIACAO);
            fichaRepository.save(ficha);
        }
    }

    @Transactional
    public FichaAtendimentoDTO finalizarAtendimento(String fichaId, String usuarioId){
        FichaAtendimento ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de atendimento não encontrada: " + fichaId));

        if(ficha.getStatus() != StatusAtendimento.ALTA){
            throw new BusinessRuleException("Não é possível finalizar um atendimento que não recebeu alta médica. Status atual: " + ficha.getStatus());
        }

        ficha.setStatus(StatusAtendimento.FINALIZADO);
        ficha.setAtiva(true);
        ficha.setDataHoraSaida(LocalDateTime.now());

        fichaRepository.save(ficha);
        return new FichaAtendimentoDTO(ficha);
    }
}
