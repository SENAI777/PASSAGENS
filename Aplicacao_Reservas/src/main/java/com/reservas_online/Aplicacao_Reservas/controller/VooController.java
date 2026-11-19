package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.service.VooService;
import com.reservas_online.Aplicacao_Reservas.service.VooService.ResultadoSalvar;

@Controller
@RequestMapping("/voos")
public class VooController {

    private final VooService vooService;

    public VooController(VooService vooService) {
        this.vooService = vooService;
    }

    @GetMapping
    public String listar(Model model) {
        VooService.DadosListagem listagem = vooService.listar();
        model.addAttribute("voos", listagem.voos());
        model.addAttribute("qtdReservas", listagem.qtdReservas());
        return "voos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("voo", new Voo());
        return "voos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return vooService.buscarPorId(id)
                .map(v -> {
                    model.addAttribute("voo", v);
                    return "voos/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Voo não encontrado.");
                    return "redirect:/voos";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Voo voo, Model model, RedirectAttributes ra) {
        if (vooService.salvar(voo) == ResultadoSalvar.ORIGEM_E_DESTINO_IGUAIS) {
            model.addAttribute("erro", "A origem e o destino precisam ser diferentes.");
            return "voos/form";
        }
        ra.addFlashAttribute("sucesso", "Voo salvo com sucesso.");
        return "redirect:/voos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (!vooService.excluir(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o voo possui reservas.");
        } else {
            ra.addFlashAttribute("sucesso", "Voo excluído.");
        }
        return "redirect:/voos";
    }
}
