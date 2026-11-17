package com.reservas_online.Aplicacao_Reservas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "passageiro")
public class Passageiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT do MySQL
    @Column(name = "id_passageiro")
    private Long codPassageiro;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "idade", nullable = false)
    private Integer idade;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    public Passageiro() {
    }

    public Passageiro(Long codPassageiro, String nome, String cpf, Integer idade, String email) {
        this.codPassageiro = codPassageiro;
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
        this.email = email;
    }

    public Long getCodPassageiro() {
        return codPassageiro;
    }

    public void setCodPassageiro(Long codPassageiro) {
        this.codPassageiro = codPassageiro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
