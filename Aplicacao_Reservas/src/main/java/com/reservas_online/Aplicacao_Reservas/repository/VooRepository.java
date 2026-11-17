package com.reservas_online.Aplicacao_Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_online.Aplicacao_Reservas.model.Voo;

public interface VooRepository extends JpaRepository<Voo, Long> {
}
