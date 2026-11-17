package com.reservas_online.Aplicacao_Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_online.Aplicacao_Reservas.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // O Spring Data monta o SQL a partir do nome do método:
    // existsByPassageiroCodPassageiro -> ... WHERE reserva.id_passageiro = ?
    boolean existsByPassageiroCodPassageiro(Long codPassageiro);

    boolean existsByVooCodVoo(Long codVoo);

    long countByVooCodVoo(Long codVoo);
}
