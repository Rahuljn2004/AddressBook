package com.example.addressbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailSenderService class is responsible for sending emails.
 * It uses JavaMailSender to send emails.
 */
@Service
public class EmailSenderService {

    //JavaMailSender is used to send emails.
    @Autowired
    JavaMailSender mailSender;

    /**
     * This method sends an email to the specified recipient with the given subject and body.
     *
     * @param toEmail - The recipient's email address.
     * @param subject - The subject of the email.
     * @param body    - The body of the email.
     */
    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("arunodaya9027p.sg@gmail.com");
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setText(body);
        simpleMailMessage.setSubject(subject);
        mailSender.send(simpleMailMessage);
        System.out.println("Mail send to the user!!");
    }
}