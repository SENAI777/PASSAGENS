package com.reservas_online.Aplicacao_Reservas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.reservas_online.Aplicacao_Reservas.model.Passageiro;
import com.reservas_online.Aplicacao_Reservas.model.Reserva;
import com.reservas_online.Aplicacao_Reservas.model.Voo;
import com.reservas_online.Aplicacao_Reservas.service.EmailService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
class EmailReservaTests {

    @MockitoBean
    private JavaMailSender mailSender; // servidor de e-mail falso

    @Autowired
    private EmailService emailService;

    @Test
    void enviaEmailDeConfirmacaoParaOPassageiro() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        Passageiro passageiro = new Passageiro(1L, "Ana Silva", "123.456.789-00", 30, "ana@exemplo.com");
        Voo voo = new Voo(1L, "São Paulo", "Recife", LocalDateTime.of(2026, 12, 1, 8, 30), "Airbus A320");
        Reserva reserva = new Reserva(7L, new BigDecimal("599.90"), 2, passageiro, voo);
        reserva.setLocalizador("K7XQ2M");
        reserva.setStatus("Confirmada");

        boolean enviado = emailService.enviarConfirmacaoReserva(reserva);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage email = captor.getValue();

        assertThat(enviado).isTrue();
        assertThat(email.getAllRecipients()[0].toString()).isEqualTo("ana@exemplo.com");
        assertThat(email.getSubject()).contains("Reserva confirmada - localizador K7XQ2M");
        String corpo = (String) email.getContent();
        assertThat(corpo).contains("K7XQ2M", "Confirmada", "Ana Silva", "São Paulo → Recife", "01/12/2026 08:30", "R$ 599,90");
    }
}
