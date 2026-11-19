package com.reservas_online.Aplicacao_Reservas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;
import com.reservas_online.Aplicacao_Reservas.repository.VooRepository;

@Service
public class VooService {

    private final VooRepository vooRepository;
    private final ReservaRepository reservaRepository;

    public VooService(VooRepository vooRepository, ReservaRepository reservaRepository) {
        this.vooRepository = vooRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public DadosListagem listar() {
        List<Voo> voos = vooRepository.findAll();
        Map<Long, Long> qtdReservas = new HashMap<>();
        for (Voo voo : voos) {
            qtdReservas.put(voo.getCodVoo(), reservaRepository.countByVooCodVoo(voo.getCodVoo()));
        }
        return new DadosListagem(voos, qtdReservas);
    }

    @Transactional(readOnly = true)
    public Optional<Voo> buscarPorId(Long id) {
        return vooRepository.findById(id);
    }

    @Transactional
    public ResultadoSalvar salvar(Voo voo) {
        if (voo.getOrigem() != null && voo.getOrigem().trim().equalsIgnoreCase(
                voo.getDestino() == null ? "" : voo.getDestino().trim())) {
            return ResultadoSalvar.ORIGEM_E_DESTINO_IGUAIS;
        }

        vooRepository.save(voo);
        return ResultadoSalvar.SALVO;
    }

    @Transactional
    public boolean excluir(Long id) {
        if (reservaRepository.existsByVooCodVoo(id)) {
            return false;
        }

        vooRepository.deleteById(id);
        return true;
    }

    public record DadosListagem(List<Voo> voos, Map<Long, Long> qtdReservas) {
    }

    public enum ResultadoSalvar {
        SALVO,
        ORIGEM_E_DESTINO_IGUAIS
    }
}
