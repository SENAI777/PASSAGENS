package com.reservas_online.Aplicacao_Reservas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.reservas_online.Aplicacao_Reservas.dto.ReservaRequestDTO;
import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.service.PassageiroService;
import com.reservas_online.Aplicacao_Reservas.service.VooService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class RestControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PassageiroService passageiroService;

    @Autowired
    private VooService vooService;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void configurarMocks() {
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void fluxoCompletoApiPassageiros() throws Exception {
        Passageiro passageiro = new Passageiro(null, "Empresa Parceira User", "999.888.777-66", 28, "parceiro@empresa.com");

        // 1. POST
        String responseContent = mockMvc.perform(post("/api/passageiros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passageiro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Empresa Parceira User"))
                .andReturn().getResponse().getContentAsString();

        Passageiro criado = objectMapper.readValue(responseContent, Passageiro.class);
        assertThat(criado.getCodPassageiro()).isNotNull();

        // 2. GET by ID
        mockMvc.perform(get("/api/passageiros/" + criado.getCodPassageiro()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("parceiro@empresa.com"));

        // 3. GET all
        mockMvc.perform(get("/api/passageiros"))
                .andExpect(status().isOk());

        // 4. PUT
        criado.setNome("Empresa Parceira Atualizado");
        mockMvc.perform(put("/api/passageiros/" + criado.getCodPassageiro())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Empresa Parceira Atualizado"));

        // 5. DELETE
        mockMvc.perform(delete("/api/passageiros/" + criado.getCodPassageiro()))
                .andExpect(status().isNoContent());
    }

    @Test
    void fluxoCompletoApiVoos() throws Exception {
        Voo voo = new Voo(null, "Campinas", "Brasília", LocalDateTime.of(2027, 5, 10, 14, 0), "Boeing 737");

        // 1. POST
        String responseContent = mockMvc.perform(post("/api/voos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origem").value("Campinas"))
                .andReturn().getResponse().getContentAsString();

        Voo criado = objectMapper.readValue(responseContent, Voo.class);
        assertThat(criado.getCodVoo()).isNotNull();

        // 2. Validação origem == destino
        Voo vooInvalido = new Voo(null, "Campinas", "Campinas", LocalDateTime.now(), "Boeing 737");
        mockMvc.perform(post("/api/voos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vooInvalido)))
                .andExpect(status().isBadRequest());

        // 3. GET by ID
        mockMvc.perform(get("/api/voos/" + criado.getCodVoo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino").value("Brasília"));

        // 4. DELETE
        mockMvc.perform(delete("/api/voos/" + criado.getCodVoo()))
                .andExpect(status().isNoContent());
    }

    @Test
    void fluxoCompletoApiReservas() throws Exception {
        Passageiro p = new Passageiro(null, "Passageiro Reserva", "111.222.333-44", 32, "reserva.api@empresa.com");
        passageiroService.salvar(p);

        Voo v = new Voo(null, "Curitiba", "Fortaleza", LocalDateTime.of(2027, 6, 1, 10, 30), "Embraer 195");
        vooService.salvar(v);

        ReservaRequestDTO dto = new ReservaRequestDTO(p.getCodPassageiro(), null, v.getCodVoo(), null, new BigDecimal("750.00"), 1);

        // 1. POST
        String responseContent = mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.localizador").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Confirmada"))
                .andReturn().getResponse().getContentAsString();

        // 2. Tentar excluir passageiro que tem reserva (deve falhar com 409 Conflict)
        mockMvc.perform(delete("/api/passageiros/" + p.getCodPassageiro()))
                .andExpect(status().isConflict());

        // 3. GET all reservas
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk());
    }
}
