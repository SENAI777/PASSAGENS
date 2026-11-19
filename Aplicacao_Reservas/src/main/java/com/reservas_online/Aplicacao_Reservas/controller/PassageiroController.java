package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.service.PassageiroService;
import com.reservas_online.Aplicacao_Reservas.service.PassageiroService.ResultadoSalvar;

@Controller
@RequestMapping("/passageiros")
public class PassageiroController {

    private final PassageiroService passageiroService;

    public PassageiroController(PassageiroService passageiroService) {
        this.passageiroService = passageiroService;
    }

    // READ - lista todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("passageiros", passageiroService.listar());
        return "passageiros/lista";
    }

    // CREATE - abre formulário vazio
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("passageiro", new Passageiro());
        return "passageiros/form";
    }

    // UPDATE - abre formulário preenchido
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return passageiroService.buscarPorId(id)
                .map(p -> {
                    model.addAttribute("passageiro", p);
                    return "passageiros/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Passageiro não encontrado.");
                    return "redirect:/passageiros";
                });
    }

    // CREATE / UPDATE - salva
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Passageiro passageiro, Model model, RedirectAttributes ra) {
        ResultadoSalvar resultado = passageiroService.salvar(passageiro);
        if (resultado == ResultadoSalvar.CPF_DUPLICADO || resultado == ResultadoSalvar.EMAIL_DUPLICADO) {
            model.addAttribute("erro", resultado == ResultadoSalvar.CPF_DUPLICADO
                    ? "Já existe um passageiro com este CPF."
                    : "Já existe um passageiro com este e-mail.");
            return "passageiros/form";
        }

        ra.addFlashAttribute("sucesso", "Passageiro salvo com sucesso.");
        return "redirect:/passageiros";
    }

    // DELETE
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (!passageiroService.excluir(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o passageiro possui reservas.");
        } else {
            ra.addFlashAttribute("sucesso", "Passageiro excluído.");
        }
        return "redirect:/passageiros";
    }
}
