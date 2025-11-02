package com.sistema_hospitalar.controller;

import com.sistema_hospitalar.dto.FichaAtendimentoDTO;
import com.sistema_hospitalar.service.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filas")
@Tag(name = "Filas de Espera", description = "Endpoints para consultar as filas de atendimento")
@SecurityRequirement(name = "bearerAuth")
public class FilaController {

    @Autowired
    private AtendimentoService atendimentoService;

    @GetMapping("/triagem")
    @PreAuthorize("hasRole('ENFERMEIRO')")
    @Operation(summary = "Lista pacientes aguardando triagem", description = "Retorna a lista de pacientes com status AGUARDANDO_TRIAGEM.")
    public ResponseEntity<List<FichaAtendimentoDTO>> getFilaTriagem() {
        List<FichaAtendimentoDTO> fila = atendimentoService.listarAguardandoTriagem();
        return ResponseEntity.ok(fila);
    }
}
