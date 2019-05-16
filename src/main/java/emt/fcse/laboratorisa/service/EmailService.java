package emt.fcse.laboratorisa.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String message);
}
