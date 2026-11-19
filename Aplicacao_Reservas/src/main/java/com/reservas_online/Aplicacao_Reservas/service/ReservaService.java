package com.reservas_online.Aplicacao_Reservas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.repository.PassageiroRepository;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;
import com.reservas_online.Aplicacao_Reservas.repository.VooRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PassageiroRepository passageiroRepository;
    private final VooRepository vooRepository;
    private final EmailService emailService;

    public ReservaService(ReservaRepository reservaRepository,
                         PassageiroRepository passageiroRepository,
                         VooRepository vooRepository,
                         EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.passageiroRepository = passageiroRepository;
        this.vooRepository = vooRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean podeCriarReserva() {
        return passageiroRepository.count() > 0 && vooRepository.count() > 0;
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Passageiro> listarPassageiros() {
        return passageiroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Voo> listarVoos() {
        return vooRepository.findAll();
    }

    @Transactional
    public ResultadoSalvar salvar(Reserva reserva) {
        Long codPassageiro = reserva.getPassageiro() == null
                ? null
                : reserva.getPassageiro().getCodPassageiro();
        Long codVoo = reserva.getVoo() == null ? null : reserva.getVoo().getCodVoo();

        Optional<Passageiro> passageiro = codPassageiro == null
                ? Optional.empty()
                : passageiroRepository.findById(codPassageiro);
        Optional<Voo> voo = codVoo == null
                ? Optional.empty()
                : vooRepository.findById(codVoo);

        if (passageiro.isEmpty() || voo.isEmpty()) {
            return new ResultadoSalvar(StatusSalvar.DADOS_INVALIDOS, reserva, false);
        }

        boolean novaReserva = reserva.getCodReserva() == null;
        if (!novaReserva) {
            Optional<Reserva> existente = reservaRepository.findById(reserva.getCodReserva());
            if (existente.isEmpty()) {
                return new ResultadoSalvar(StatusSalvar.NAO_ENCONTRADA, reserva, false);
            }

            Reserva dadosFormulario = reserva;
            reserva = existente.get();
            reserva.setTarifa(dadosFormulario.getTarifa());
            reserva.setQtdBagagens(dadosFormulario.getQtdBagagens());
        }

        reserva.setPassageiro(passageiro.get());
        reserva.setVoo(voo.get());
        Reserva salva = reservaRepository.save(reserva);

        if (!novaReserva) {
            return new ResultadoSalvar(StatusSalvar.ATUALIZADA, salva, false);
        }

        return new ResultadoSalvar(StatusSalvar.CRIADA, salva, emailService.enviarConfirmacaoReserva(salva));
    }

    @Transactional
    public void excluir(Long id) {
        reservaRepository.deleteById(id);
    }

    public record ResultadoSalvar(StatusSalvar status, Reserva reserva, boolean emailEnviado) {
    }

    public enum StatusSalvar {
        DADOS_INVALIDOS,
        NAO_ENCONTRADA,
        CRIADA,
        ATUALIZADA
    }
}
