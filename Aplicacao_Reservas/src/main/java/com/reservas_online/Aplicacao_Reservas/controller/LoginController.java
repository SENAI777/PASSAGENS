package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reservas_online.Aplicacao_Reservas.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@RequestParam String usuario,
                         @RequestParam String senha,
                         HttpSession session,
                         RedirectAttributes ra) {
        var autenticado = loginService.autenticar(usuario, senha);
        if (autenticado.isEmpty()) {
            ra.addFlashAttribute("erro", "Usuário ou senha inválidos.");
            return "redirect:/login";
        }

        session.setAttribute("usuarioLogado", autenticado.get().getUsuario());
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String sair(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
