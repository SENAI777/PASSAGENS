package com.reservas_online.Aplicacao_Reservas.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.service.PassageiroService;
import com.reservas_online.Aplicacao_Reservas.service.PassageiroService.ResultadoSalvar;

@RestController
@RequestMapping("/api/passageiros")
public class PassageiroRestController {

    private final PassageiroService passageiroService;

    public PassageiroRestController(PassageiroService passageiroService) {
        this.passageiroService = passageiroService;
    }

    @GetMapping
    public List<Passageiro> listar() {
        return passageiroService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passageiro> buscarPorId(@PathVariable Long id) {
        return passageiroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Passageiro passageiro) {
        ResultadoSalvar resultado = passageiroService.salvar(passageiro);
        if (resultado == ResultadoSalvar.CPF_DUPLICADO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Já existe um passageiro cadastrado com este CPF."));
        }
        if (resultado == ResultadoSalvar.EMAIL_DUPLICADO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Já existe um passageiro cadastrado com este e-mail."));
        }

        URI location = URI.create("/api/passageiros/" + passageiro.getCodPassageiro());
        return ResponseEntity.created(location).body(passageiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Passageiro passageiro) {
        if (passageiroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        passageiro.setCodPassageiro(id);
        ResultadoSalvar resultado = passageiroService.salvar(passageiro);

        if (resultado == ResultadoSalvar.CPF_DUPLICADO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Já existe outro passageiro com este CPF."));
        }
        if (resultado == ResultadoSalvar.EMAIL_DUPLICADO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Já existe outro passageiro com este e-mail."));
        }

        return ResponseEntity.ok(passageiro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (passageiroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!passageiroService.excluir(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Não é possível excluir: o passageiro possui reservas associadas."));
        }

        return ResponseEntity.noContent().build();
    }
}
