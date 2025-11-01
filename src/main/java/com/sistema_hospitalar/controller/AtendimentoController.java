package com.sistema_hospitalar.controller;

import com.sistema_hospitalar.dto.FichaAtendimentoDTO;
import com.sistema_hospitalar.dto.IniciarAtendimentoRequestDTO;
import com.sistema_hospitalar.service.AtendimentoService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atendimentos")
@Tag(name = "Fluxo de Atendimento", description = "Endpoints que gerenciam o fluxo do paciente")
@SecurityRequirement(name = "bearerAuth")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @PostMapping("/iniciar")
    @PreAuthorize("hasRole('ATENDENTE')")
    @Operation(summary = "Inicia um novo atendimento (Abre a ficha)", description = "Cria uma ficha de atendimento para um paciente existente. Status inicial: AGUARDANDO_TRIAGEM.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ficha criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Paciente já possui atendimento ativo"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<FichaAtendimentoDTO> iniciarAtendimento(@RequestBody @Valid IniciarAtendimentoRequestDTO dto){
        FichaAtendimentoDTO novaFicha = atendimentoService.iniciarAtendimento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaFicha);
    }
}
