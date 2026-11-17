package com.reservas_online.Aplicacao_Reservas.model;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "reserva")
public class Reserva {

    private static final String LETRAS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long codReserva;

    @Column(name = "tarifa", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifa;

    @Column(name = "qtd_bagagens", nullable = false)
    private Integer qtdBagagens;

    @Column(name = "localizador", nullable = false, unique = true, length = 20)
    private String localizador;

    @Column(name = "data_reserva", nullable = false)
    private LocalDateTime dataReserva;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Muitas reservas -> um passageiro (coluna id_passageiro)
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_passageiro", nullable = false)
    private Passageiro passageiro = new Passageiro();

    // Muitas reservas -> um voo (coluna id_voo)
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_voo", nullable = false)
    private Voo voo = new Voo();

    public Reserva() {
    }

    public Reserva(Long codReserva, BigDecimal tarifa, Integer qtdBagagens, Passageiro passageiro, Voo voo) {
        this.codReserva = codReserva;
        this.tarifa = tarifa;
        this.qtdBagagens = qtdBagagens;
        this.passageiro = passageiro;
        this.voo = voo;
    }

    /** Executado automaticamente antes do INSERT: preenche os campos gerados pelo sistema. */
    @PrePersist
    void antesDeInserir() {
        if (localizador == null) {
            localizador = gerarLocalizador();
        }
        if (dataReserva == null) {
            dataReserva = LocalDateTime.now();
        }
        if (status == null) {
            status = "Confirmada";
        }
    }

    /** Código de 6 caracteres, como os das companhias aéreas (ex.: K7XQ2M). */
    private static String gerarLocalizador() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(LETRAS.charAt(RANDOM.nextInt(LETRAS.length())));
        }
        return sb.toString();
    }

    public Long getCodReserva() {
        return codReserva;
    }

    public void setCodReserva(Long codReserva) {
        this.codReserva = codReserva;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public Integer getQtdBagagens() {
        return qtdBagagens;
    }

    public void setQtdBagagens(Integer qtdBagagens) {
        this.qtdBagagens = qtdBagagens;
    }

    public String getLocalizador() {
        return localizador;
    }

    public void setLocalizador(String localizador) {
        this.localizador = localizador;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Passageiro getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(Passageiro passageiro) {
        this.passageiro = passageiro;
    }

    public Voo getVoo() {
        return voo;
    }

    public void setVoo(Voo voo) {
        this.voo = voo;
    }
}
