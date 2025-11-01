package com.sistema_hospitalar.controller;

import com.sistema_hospitalar.dto.PacienteDTO;
import com.sistema_hospitalar.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Pacientes", description = "Gerenciamento de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    @PreAuthorize("hasRole('ATENDENTE')")
    @Operation(summary = "Cria um novo paciente", description = "Registra um novo paciente no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já cadastrado.")
    })
    public ResponseEntity<PacienteDTO.Response> criarPaciente(@RequestBody @Valid PacienteDTO.Create dto){
        PacienteDTO.Response novoPaciente = pacienteService.criarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPaciente);
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ATENDENTE')")
    @Operation(summary = "Busca por paciente por CPF", description = "Verifica a existência de um paciente por CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<PacienteDTO.Response> buscarPorCpf(@PathVariable String cpf){
        PacienteDTO.Response paciente = pacienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(paciente);
    }
}
