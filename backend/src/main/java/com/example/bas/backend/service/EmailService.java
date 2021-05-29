package com.example.bas.backend.service;

public interface EmailService {
    void send(String to, String subject, String text);

    void sendStats(String to, String username);

}
