package com.sistema_hospitalar.repository;

import com.sistema_hospitalar.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    UserDetails findByUsername(String username);
}
