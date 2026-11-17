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
import com.reservas_online.Aplicacao_Reservas.repository.PassageiroRepository;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;

@Controller
@RequestMapping("/passageiros")
public class PassageiroController {

    private final PassageiroRepository passageiroRepository;
    private final ReservaRepository reservaRepository;

    public PassageiroController(PassageiroRepository passageiroRepository, ReservaRepository reservaRepository) {
        this.passageiroRepository = passageiroRepository;
        this.reservaRepository = reservaRepository;
    }

    // READ - lista todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("passageiros", passageiroRepository.findAll());
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
        return passageiroRepository.findById(id)
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
        // CPF e e-mail são UNIQUE no banco: avisamos antes de tentar gravar
        Long id = passageiro.getCodPassageiro();
        boolean cpfRepetido = id == null
                ? passageiroRepository.existsByCpf(passageiro.getCpf())
                : passageiroRepository.existsByCpfAndCodPassageiroNot(passageiro.getCpf(), id);
        boolean emailRepetido = id == null
                ? passageiroRepository.existsByEmail(passageiro.getEmail())
                : passageiroRepository.existsByEmailAndCodPassageiroNot(passageiro.getEmail(), id);

        if (cpfRepetido || emailRepetido) {
            model.addAttribute("erro", cpfRepetido
                    ? "Já existe um passageiro com este CPF."
                    : "Já existe um passageiro com este e-mail.");
            return "passageiros/form";
        }

        passageiroRepository.save(passageiro);
        ra.addFlashAttribute("sucesso", "Passageiro salvo com sucesso.");
        return "redirect:/passageiros";
    }

    // DELETE
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (reservaRepository.existsByPassageiroCodPassageiro(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o passageiro possui reservas.");
        } else {
            passageiroRepository.deleteById(id);
            ra.addFlashAttribute("sucesso", "Passageiro excluído.");
        }
        return "redirect:/passageiros";
    }
}
