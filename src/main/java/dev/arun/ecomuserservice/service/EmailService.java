package dev.arun.ecomuserservice.service;

import dev.arun.ecomuserservice.dto.SendEmailMessageDto;

public interface EmailService {
        void sendEmail(SendEmailMessageDto emailMessage);
    }
