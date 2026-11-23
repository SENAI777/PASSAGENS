package com.reservas_online.Aplicacao_Reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        model.addAttribute("usuarioEhAdmin",
                Boolean.TRUE.equals(session.getAttribute("usuarioEhAdmin")));
        return "index";
    }
}
