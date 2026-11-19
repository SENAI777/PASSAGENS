package com.reservas_online.Aplicacao_Reservas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.repository.PassageiroRepository;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;

@Service
public class PassageiroService {

    private final PassageiroRepository passageiroRepository;
    private final ReservaRepository reservaRepository;

    public PassageiroService(PassageiroRepository passageiroRepository, ReservaRepository reservaRepository) {
        this.passageiroRepository = passageiroRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<Passageiro> listar() {
        return passageiroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Passageiro> buscarPorId(Long id) {
        return passageiroRepository.findById(id);
    }

    @Transactional
    public ResultadoSalvar salvar(Passageiro passageiro) {
        Long id = passageiro.getCodPassageiro();
        boolean cpfRepetido = id == null
                ? passageiroRepository.existsByCpf(passageiro.getCpf())
                : passageiroRepository.existsByCpfAndCodPassageiroNot(passageiro.getCpf(), id);
        if (cpfRepetido) {
            return ResultadoSalvar.CPF_DUPLICADO;
        }

        boolean emailRepetido = id == null
                ? passageiroRepository.existsByEmail(passageiro.getEmail())
                : passageiroRepository.existsByEmailAndCodPassageiroNot(passageiro.getEmail(), id);
        if (emailRepetido) {
            return ResultadoSalvar.EMAIL_DUPLICADO;
        }

        passageiroRepository.save(passageiro);
        return ResultadoSalvar.SALVO;
    }

    @Transactional
    public boolean excluir(Long id) {
        if (reservaRepository.existsByPassageiroCodPassageiro(id)) {
            return false;
        }

        passageiroRepository.deleteById(id);
        return true;
    }

    public enum ResultadoSalvar {
        SALVO,
        CPF_DUPLICADO,
        EMAIL_DUPLICADO
    }
}
