package com.absabanking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * @Author : Simbarashe Makwangudze
 */
@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.host}")
    private String mailHost;
    @Value("${spring.mail.port}")
    private String emailPort;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String emailAuth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String startTsls;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    private Environment environment;



 /*   public void sendEmail(String to, String subject, String text, String from) {
        final com.absabanking.dto.SimpleMailMessage email = new com.absabanking.dto.SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(text);
        email.setFrom(from);
        log.info("Sending email to : {}", to);
        javaMailSender.send(email);
        log.info("Done sending email to : {}", to);
    }*/

    /**
     *
     * @param to
     * @param subject
     * @param text
     * @param from
     * @param cc
     * @throws MessagingException
     */
    public void sendHtmlEmail(String to, String subject, String text, String from, String cc) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailHost);
        properties.put("mail.smtp.port", emailPort);
        properties.put("mail.smtp.auth", emailAuth);
        properties.put("mail.smtp.starttls.enable", startTsls);

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);

            }
        };

        Session session = Session.getInstance(properties, auth);

        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        InternetAddress[] toAddresses = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        if (cc != null) {
            InternetAddress[] ccAddresses = {new InternetAddress(cc)};
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
        }
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setContent(text, "text/html");

        log.info("Sending HTML Email...");
        Transport.send(msg);
        log.info("Done sending html email for user: {}", to);
    }

    /**
     *
     * @param to
     * @param subject
     * @param text
     * @param from
     * @param filePathAndName
     * @param cc
     * @param outputFileName
     * @throws MessagingException
     * @throws IOException
     */
    public void sendEmailWithAttachment(String to, String subject, String text, String from, String filePathAndName, String cc, String outputFileName) throws MessagingException, IOException {
        log.info("About to send email with attachment to {}", to);
        log.info("File to be attached : {}", filePathAndName);
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailHost);
        properties.put("mail.smtp.starttls.enable", startTsls);
        properties.put("mail.smtp.auth", emailAuth);
        properties.put("mail.smtp.port", emailPort);


        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        if (cc != null) {
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
        }
        message.setSubject(subject);
        message.setContent(text, "text/html");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();

        attachmentBodyPart.attachFile(filePathAndName);
        log.info("The file name is {} ", attachmentBodyPart.getFileName());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);

        log.info("Sending email...");
        Transport.send(message);
        log.info("Done sending email for user: {}", to);
    }


}
