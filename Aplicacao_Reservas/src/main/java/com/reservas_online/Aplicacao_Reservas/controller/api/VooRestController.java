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

import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.service.VooService;
import com.reservas_online.Aplicacao_Reservas.service.VooService.ResultadoSalvar;

@RestController
@RequestMapping("/api/voos")
public class VooRestController {

    private final VooService vooService;

    public VooRestController(VooService vooService) {
        this.vooService = vooService;
    }

    @GetMapping
    public List<Voo> listar() {
        return vooService.listar().voos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voo> buscarPorId(@PathVariable Long id) {
        return vooService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Voo voo) {
        ResultadoSalvar resultado = vooService.salvar(voo);
        if (resultado == ResultadoSalvar.ORIGEM_E_DESTINO_IGUAIS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "A origem e o destino precisam ser diferentes."));
        }

        URI location = URI.create("/api/voos/" + voo.getCodVoo());
        return ResponseEntity.created(location).body(voo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Voo voo) {
        if (vooService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        voo.setCodVoo(id);
        ResultadoSalvar resultado = vooService.salvar(voo);
        if (resultado == ResultadoSalvar.ORIGEM_E_DESTINO_IGUAIS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "A origem e o destino precisam ser diferentes."));
        }

        return ResponseEntity.ok(voo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (vooService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!vooService.excluir(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Não é possível excluir: o voo possui reservas associadas."));
        }

        return ResponseEntity.noContent().build();
    }
}
