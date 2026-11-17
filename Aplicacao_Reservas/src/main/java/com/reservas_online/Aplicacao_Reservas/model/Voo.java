package com.reservas_online.Aplicacao_Reservas.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "voo")
public class Voo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voo")
    private Long codVoo;

    @Column(name = "origem", nullable = false, length = 100)
    private String origem;

    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    // mesmo formato que o <input type="datetime-local"> envia
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "horario", nullable = false)
    private LocalDateTime horario;

    @Column(name = "modelo_aviao", nullable = false, length = 100)
    private String modeloAviao;

    // Um voo tem várias reservas: a chave estrangeira (id_voo) fica na tabela reserva.

    public Voo() {
    }

    public Voo(Long codVoo, String origem, String destino, LocalDateTime horario, String modeloAviao) {
        this.codVoo = codVoo;
        this.origem = origem;
        this.destino = destino;
        this.horario = horario;
        this.modeloAviao = modeloAviao;
    }

    public Long getCodVoo() {
        return codVoo;
    }

    public void setCodVoo(Long codVoo) {
        this.codVoo = codVoo;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getModeloAviao() {
        return modeloAviao;
    }

    public void setModeloAviao(String modeloAviao) {
        this.modeloAviao = modeloAviao;
    }

    /** Texto curto usado nas listas e no select de reservas. */
    public String getDescricao() {
        return origem + " → " + destino;
    }
}
