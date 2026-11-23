package com.reservas_online.Aplicacao_Reservas.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.reservas_online.Aplicacao_Reservas.model.Usuario;
import com.reservas_online.Aplicacao_Reservas.repository.UsuarioRepository;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String usuario, String senha) {
        Optional<Usuario> porUsuario = usuarioRepository.findByUsuarioAndSenha(usuario, senha);
        if (porUsuario.isPresent()) {
            return porUsuario;
        }
        String cpf = UsuarioService.normalizarCpf(usuario);
        return usuarioRepository.findByCpfAndSenha(cpf, senha);
    }
}
