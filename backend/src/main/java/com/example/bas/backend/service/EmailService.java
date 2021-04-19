package com.example.bas.backend.service;

public interface EmailService {
    void send(String to, String subject, String text);
}
