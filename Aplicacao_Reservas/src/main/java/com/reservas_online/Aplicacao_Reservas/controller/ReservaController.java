package com.reservas_online.Aplicacao_Reservas.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.repository.PassageiroRepository;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;
import com.reservas_online.Aplicacao_Reservas.repository.VooRepository;
import com.reservas_online.Aplicacao_Reservas.service.EmailService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final PassageiroRepository passageiroRepository;
    private final VooRepository vooRepository;
    private final EmailService emailService;

    public ReservaController(ReservaRepository reservaRepository,
                             PassageiroRepository passageiroRepository,
                             VooRepository vooRepository,
                             EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.passageiroRepository = passageiroRepository;
        this.vooRepository = vooRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "reservas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model, RedirectAttributes ra) {
        if (passageiroRepository.findAll().isEmpty() || vooRepository.findAll().isEmpty()) {
            ra.addFlashAttribute("erro", "Cadastre ao menos um passageiro e um voo antes de criar uma reserva.");
            return "redirect:/reservas";
        }
        return abrirFormulario(new Reserva(), model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Reserva> reserva = reservaRepository.findById(id);
        if (reserva.isEmpty()) {
            ra.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/reservas";
        }
        return abrirFormulario(reserva.get(), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Reserva reserva, Model model, RedirectAttributes ra) {
        // O formulário envia só os códigos; aqui buscamos os objetos completos.
        Long codPassageiro = reserva.getPassageiro() == null ? null : reserva.getPassageiro().getCodPassageiro();
        Long codVoo = reserva.getVoo() == null ? null : reserva.getVoo().getCodVoo();
        Optional<Passageiro> passageiro = codPassageiro == null ? Optional.empty() : passageiroRepository.findById(codPassageiro);
        Optional<Voo> voo = codVoo == null ? Optional.empty() : vooRepository.findById(codVoo);

        if (passageiro.isEmpty() || voo.isEmpty()) {
            model.addAttribute("erro", "Selecione um passageiro e um voo válidos.");
            return abrirFormulario(reserva, model);
        }

        boolean novaReserva = reserva.getCodReserva() == null;

        if (!novaReserva) {
            // Edição: parte da reserva gravada para não perder localizador, data e status,
            // que não vêm no formulário
            Optional<Reserva> existente = reservaRepository.findById(reserva.getCodReserva());
            if (existente.isEmpty()) {
                ra.addFlashAttribute("erro", "Reserva não encontrada.");
                return "redirect:/reservas";
            }
            Reserva r = existente.get();
            r.setTarifa(reserva.getTarifa());
            r.setQtdBagagens(reserva.getQtdBagagens());
            reserva = r;
        }

        reserva.setPassageiro(passageiro.get());
        reserva.setVoo(voo.get());
        reserva = reservaRepository.save(reserva); // o localizador é gerado aqui (@PrePersist)

        if (novaReserva) {
            // Reserva confirmada: avisa o passageiro por e-mail
            if (emailService.enviarConfirmacaoReserva(reserva)) {
                ra.addFlashAttribute("sucesso", "Reserva " + reserva.getLocalizador()
                        + " confirmada! E-mail enviado para " + reserva.getPassageiro().getEmail() + ".");
            } else {
                ra.addFlashAttribute("sucesso", "Reserva " + reserva.getLocalizador() + " confirmada.");
                ra.addFlashAttribute("erro", "Não foi possível enviar o e-mail de confirmação. "
                        + "Verifique a configuração de e-mail.");
            }
        } else {
            ra.addFlashAttribute("sucesso", "Reserva atualizada com sucesso.");
        }
        return "redirect:/reservas";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        reservaRepository.deleteById(id);
        ra.addFlashAttribute("sucesso", "Reserva excluída.");
        return "redirect:/reservas";
    }

    private String abrirFormulario(Reserva reserva, Model model) {
        model.addAttribute("reserva", reserva);
        model.addAttribute("passageiros", passageiroRepository.findAll());
        model.addAttribute("voos", vooRepository.findAll());
        return "reservas/form";
    }
}
