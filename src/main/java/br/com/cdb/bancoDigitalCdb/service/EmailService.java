package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.MailBodyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String userNameSecret;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleMessage(MailBodyDTO mailBody){
        SimpleMailMessage massage = new SimpleMailMessage();
        massage.setTo(mailBody.to());
        massage.setFrom(userNameSecret);
        massage.setSubject(mailBody.subject());
        massage.setText(mailBody.text());

        javaMailSender.send(massage);
    }
}
