package dev.arun.ecomuserservice.service;

import dev.arun.ecomuserservice.dto.SendEmailMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(SendEmailMessageDto emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailMessage.getFrom());
        message.setTo(emailMessage.getTo());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getBody());

        javaMailSender.send(message);
    }
}
