package com.sistema_hospitalar.controller;

import com.sistema_hospitalar.domain.Usuario;
import com.sistema_hospitalar.dto.LoginRequestDTO;
import com.sistema_hospitalar.dto.LoginResponseDTO;
import com.sistema_hospitalar.dto.RegisterDTO;
import com.sistema_hospitalar.repository.UsuarioRepository;
import com.sistema_hospitalar.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var usuario = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data){
        if(this.usuarioRepository.findByUsername(data.username()) != null) {
            return ResponseEntity.badRequest().body("Username já está em uso.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        Usuario novoUsuario = new Usuario(null, data.username(), encryptedPassword, data.role());
        this.usuarioRepository.save(novoUsuario);

        return ResponseEntity.ok().body("Usuário registrado com sucesso.");
    }
}
