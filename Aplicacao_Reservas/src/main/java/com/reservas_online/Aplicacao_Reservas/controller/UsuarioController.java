package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.model.Usuario;
import com.reservas_online.Aplicacao_Reservas.service.UsuarioService;
import com.reservas_online.Aplicacao_Reservas.service.UsuarioService.ResultadoSalvar;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!ehAdmin(session)) {
            return "redirect:/";
        }
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {
        if (!ehAdmin(session)) {
            return "redirect:/";
        }
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        if (!ehAdmin(session)) {
            return "redirect:/";
        }
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    return "usuarios/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Usuário não encontrado.");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario,
                         HttpSession session,
                         Model model,
                         RedirectAttributes ra) {
        if (!ehAdmin(session)) {
            return "redirect:/";
        }

        ResultadoSalvar resultado = usuarioService.salvar(usuario);
        if (resultado == ResultadoSalvar.CPF_DUPLICADO) {
            model.addAttribute("erro", "Já existe um usuário com este CPF.");
            model.addAttribute("usuario", usuario);
            return "usuarios/form";
        }
        if (resultado == ResultadoSalvar.DADOS_INVALIDOS) {
            model.addAttribute("erro", "Preencha nome, CPF e senha.");
            model.addAttribute("usuario", usuario);
            return "usuarios/form";
        }

        ra.addFlashAttribute("sucesso", "Usuário salvo com sucesso.");
        return "redirect:/usuarios";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!ehAdmin(session)) {
            return "redirect:/";
        }
        if (!usuarioService.excluir(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir o usuário admin.");
        } else {
            ra.addFlashAttribute("sucesso", "Usuário excluído.");
        }
        return "redirect:/usuarios";
    }

    private boolean ehAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("usuarioEhAdmin"));
    }
}
