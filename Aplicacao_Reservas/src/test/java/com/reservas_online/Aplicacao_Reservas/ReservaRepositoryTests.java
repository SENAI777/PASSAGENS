package com.reservas_online.Aplicacao_Reservas;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.repository.PassageiroRepository;
import com.reservas_online.Aplicacao_Reservas.repository.ReservaRepository;
import com.reservas_online.Aplicacao_Reservas.repository.VooRepository;

/** Testa o mapeamento JPA usando o banco H2 em memória. */
@DataJpaTest
class ReservaRepositoryTests {

    @Autowired PassageiroRepository passageiroRepository;
    @Autowired VooRepository vooRepository;
    @Autowired ReservaRepository reservaRepository;

    @Test
    void salvaReservasEGeraLocalizadorDataEStatus() {
        Passageiro ana = passageiroRepository.save(
                new Passageiro(null, "Ana Silva", "123.456.789-00", 30, "ana@exemplo.com"));
        Voo voo = vooRepository.save(
                new Voo(null, "São Paulo", "Recife", LocalDateTime.of(2026, 12, 1, 8, 30), "Airbus A320"));

        // um passageiro pode ter mais de uma reserva
        Reserva r1 = reservaRepository.save(new Reserva(null, new BigDecimal("599.90"), 2, ana, voo));
        Reserva r2 = reservaRepository.save(new Reserva(null, new BigDecimal("320.00"), 0, ana, voo));

        assertThat(r1.getCodReserva()).isNotNull();
        assertThat(r1.getLocalizador()).hasSize(6);
        assertThat(r1.getLocalizador()).isNotEqualTo(r2.getLocalizador());
        assertThat(r1.getDataReserva()).isNotNull();
        assertThat(r1.getStatus()).isEqualTo("Confirmada");

        assertThat(reservaRepository.countByVooCodVoo(voo.getCodVoo())).isEqualTo(2);
        assertThat(reservaRepository.existsByPassageiroCodPassageiro(ana.getCodPassageiro())).isTrue();
        assertThat(passageiroRepository.existsByCpf("123.456.789-00")).isTrue();
    }
}
