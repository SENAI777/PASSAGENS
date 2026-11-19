package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.service.ReservaService;
import com.reservas_online.Aplicacao_Reservas.service.ReservaService.StatusSalvar;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listar());
        return "reservas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model, RedirectAttributes ra) {
        if (!reservaService.podeCriarReserva()) {
            ra.addFlashAttribute("erro", "Cadastre ao menos um passageiro e um voo antes de criar uma reserva.");
            return "redirect:/reservas";
        }
        return abrirFormulario(new Reserva(), model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var reserva = reservaService.buscarPorId(id);
        if (reserva.isEmpty()) {
            ra.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/reservas";
        }
        return abrirFormulario(reserva.get(), model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Reserva reserva, Model model, RedirectAttributes ra) {
        var resultado = reservaService.salvar(reserva);
        if (resultado.status() == StatusSalvar.DADOS_INVALIDOS) {
            model.addAttribute("erro", "Selecione um passageiro e um voo válidos.");
            return abrirFormulario(resultado.reserva(), model);
        }

        if (resultado.status() == StatusSalvar.NAO_ENCONTRADA) {
            ra.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/reservas";
        }

        if (resultado.status() == StatusSalvar.CRIADA) {
            Reserva salva = resultado.reserva();
            if (resultado.emailEnviado()) {
                ra.addFlashAttribute("sucesso", "Reserva " + salva.getLocalizador()
                        + " confirmada! E-mail enviado para " + salva.getPassageiro().getEmail() + ".");
            } else {
                ra.addFlashAttribute("sucesso", "Reserva " + salva.getLocalizador() + " confirmada.");
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
        reservaService.excluir(id);
        ra.addFlashAttribute("sucesso", "Reserva excluída.");
        return "redirect:/reservas";
    }

    private String abrirFormulario(Reserva reserva, Model model) {
        model.addAttribute("reserva", reserva);
        model.addAttribute("passageiros", reservaService.listarPassageiros());
        model.addAttribute("voos", reservaService.listarVoos());
        return "reservas/form";
    }
}
