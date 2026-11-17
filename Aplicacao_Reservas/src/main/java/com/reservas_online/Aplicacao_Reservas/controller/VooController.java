package com.reservas_online.Aplicacao_Reservas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;
import com.reservas_online.Aplicacao_Reservas.repository.VooRepository;

@Controller
@RequestMapping("/voos")
public class VooController {

    private final VooRepository vooRepository;
    private final ReservaRepository reservaRepository;

    public VooController(VooRepository vooRepository, ReservaRepository reservaRepository) {
        this.vooRepository = vooRepository;
        this.reservaRepository = reservaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        List<Voo> voos = vooRepository.findAll();
        // quantidade de reservas de cada voo (um voo tem várias reservas)
        Map<Long, Long> qtdReservas = new HashMap<>();
        for (Voo v : voos) {
            qtdReservas.put(v.getCodVoo(), reservaRepository.countByVooCodVoo(v.getCodVoo()));
        }
        model.addAttribute("voos", voos);
        model.addAttribute("qtdReservas", qtdReservas);
        return "voos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("voo", new Voo());
        return "voos/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return vooRepository.findById(id)
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
        // O banco tem CHECK (origem <> destino)
        if (voo.getOrigem() != null && voo.getOrigem().trim().equalsIgnoreCase(
                voo.getDestino() == null ? "" : voo.getDestino().trim())) {
            model.addAttribute("erro", "A origem e o destino precisam ser diferentes.");
            return "voos/form";
        }
        vooRepository.save(voo);
        ra.addFlashAttribute("sucesso", "Voo salvo com sucesso.");
        return "redirect:/voos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (reservaRepository.existsByVooCodVoo(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o voo possui reservas.");
        } else {
            vooRepository.deleteById(id);
            ra.addFlashAttribute("sucesso", "Voo excluído.");
        }
        return "redirect:/voos";
    }
}
