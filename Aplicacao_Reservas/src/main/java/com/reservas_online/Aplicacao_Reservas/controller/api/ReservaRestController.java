package com.reservas_online.Aplicacao_Reservas.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.reservas_online.Aplicacao_Reservas.dto.ReservaRequestDTO;
import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.service.ReservaService;
import com.reservas_online.Aplicacao_Reservas.service.ReservaService.ResultadoSalvar;
import com.reservas_online.Aplicacao_Reservas.service.ReservaService.StatusSalvar;

@RestController
@RequestMapping("/api/reservas")
public class ReservaRestController {

    private final ReservaService reservaService;

    public ReservaRestController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ReservaRequestDTO dto) {
        Long idPassageiro = dto.obterIdPassageiro();
        Long idVoo = dto.obterIdVoo();

        if (idPassageiro == null || idVoo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "É obrigatório informar o id do passageiro e o id do voo."));
        }

        if (dto.tarifa() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "A tarifa é obrigatória."));
        }

        Reserva reserva = new Reserva();
        Passageiro passageiro = new Passageiro();
        passageiro.setCodPassageiro(idPassageiro);
        reserva.setPassageiro(passageiro);

        Voo voo = new Voo();
        voo.setCodVoo(idVoo);
        reserva.setVoo(voo);

        reserva.setTarifa(dto.tarifa());
        reserva.setQtdBagagens(dto.qtdBagagens() != null ? dto.qtdBagagens() : 0);

        ResultadoSalvar resultado = reservaService.salvar(reserva);

        if (resultado.status() == StatusSalvar.DADOS_INVALIDOS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Passageiro ou voo informado não foi encontrado."));
        }

        Reserva salva = resultado.reserva();
        URI location = URI.create("/api/reservas/" + salva.getCodReserva());
        return ResponseEntity.created(location).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ReservaRequestDTO dto) {
        Optional<Reserva> existente = reservaService.buscarPorId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reserva = new Reserva();
        reserva.setCodReserva(id);
        reserva.setTarifa(dto.tarifa() != null ? dto.tarifa() : existente.get().getTarifa());
        reserva.setQtdBagagens(dto.qtdBagagens() != null ? dto.qtdBagagens() : existente.get().getQtdBagagens());

        Long idPassageiro = dto.obterIdPassageiro();
        Passageiro passageiro = new Passageiro();
        passageiro.setCodPassageiro(idPassageiro != null ? idPassageiro : existente.get().getPassageiro().getCodPassageiro());
        reserva.setPassageiro(passageiro);

        Long idVoo = dto.obterIdVoo();
        Voo voo = new Voo();
        voo.setCodVoo(idVoo != null ? idVoo : existente.get().getVoo().getCodVoo());
        reserva.setVoo(voo);

        ResultadoSalvar resultado = reservaService.salvar(reserva);

        if (resultado.status() == StatusSalvar.DADOS_INVALIDOS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Passageiro ou voo informado não foi encontrado."));
        }
        if (resultado.status() == StatusSalvar.NAO_ENCONTRADA) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultado.reserva());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (reservaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        reservaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
