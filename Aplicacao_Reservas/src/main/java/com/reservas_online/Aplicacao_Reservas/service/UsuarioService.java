package com.reservas_online.Aplicacao_Reservas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas_online.Aplicacao_Reservas.model.Usuario;
import com.reservas_online.Aplicacao_Reservas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public ResultadoSalvar salvar(Usuario usuario) {
        usuario.setCpf(normalizarCpf(usuario.getCpf()));

        if (usuario.getCpf() == null || usuario.getCpf().isBlank()
                || usuario.getNome() == null || usuario.getNome().isBlank()
                || usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            return ResultadoSalvar.DADOS_INVALIDOS;
        }

        Long id = usuario.getId();
        boolean cpfRepetido = id == null
                ? usuarioRepository.existsByCpf(usuario.getCpf())
                : usuarioRepository.existsByCpfAndIdNot(usuario.getCpf(), id);
        if (cpfRepetido) {
            return ResultadoSalvar.CPF_DUPLICADO;
        }

        usuario.setUsuario(usuario.getCpf());
        usuarioRepository.save(usuario);
        return ResultadoSalvar.SALVO;
    }

    @Transactional
    public boolean excluir(Long id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> !"admin".equals(usuario.getUsuario()))
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    return true;
                })
                .orElse(false);
    }

    public static String normalizarCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("[^0-9]", "");
    }

    public enum ResultadoSalvar {
        SALVO,
        CPF_DUPLICADO,
        DADOS_INVALIDOS
    }
}
