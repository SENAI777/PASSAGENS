package com.reservas_online.Aplicacao_Reservas.service;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.reservas_online.Aplicacao_Reservas.model.Reserva;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String remetente;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        @Value("${spring.mail.username:}") String remetente) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.remetente = remetente;
    }

    /**
     * Envia o e-mail de confirmação da reserva para o passageiro.
     *
     * @return true se enviou; false se deu erro (a reserva continua salva).
     */
    public boolean enviarConfirmacaoReserva(Reserva reserva) {
        String destinatario = reserva.getPassageiro().getEmail();
        try {
            // Monta o corpo HTML a partir do template templates/email/confirmacao-reserva.html
            Context ctx = new Context();
            ctx.setVariable("reserva", reserva);
            String html = templateEngine.process("email/confirmacao-reserva", ctx);

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, false, StandardCharsets.UTF_8.name());
            if (!remetente.isBlank()) {
                helper.setFrom(remetente);
            }
            helper.setTo(destinatario);
            helper.setSubject("Reserva confirmada - localizador " + reserva.getLocalizador()
                    + " - " + reserva.getVoo().getDescricao());
            helper.setText(html, true); // true = conteúdo HTML

            mailSender.send(mensagem);
            log.info("E-mail de confirmação da reserva {} enviado para {}", reserva.getCodReserva(), destinatario);
            return true;
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail da reserva {} para {}: {}",
                    reserva.getCodReserva(), destinatario, e.getMessage());
            return false;
        }
    }
}
