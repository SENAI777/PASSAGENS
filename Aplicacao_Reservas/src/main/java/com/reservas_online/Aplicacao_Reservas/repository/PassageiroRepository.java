package com.reservas_online.Aplicacao_Reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;

/** O Spring Data cria a implementação (findAll, findById, save, deleteById...) sozinho. */
public interface PassageiroRepository extends JpaRepository<Passageiro, Long> {

    boolean existsByCpfAndCodPassageiroNot(String cpf, Long codPassageiro);

    boolean existsByEmailAndCodPassageiroNot(String email, Long codPassageiro);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
