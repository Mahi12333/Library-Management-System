package org.librarymanagementsystem.utils;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private  JavaMailSender mailSender;

    @Value("${spring.mail.properties.domain_name}") // Domain name from application properties
    private String domainName;

    @Async
    public void sendEmail(String to, String Subject, String htmlContent, String from) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(Subject);
            //log.info("resetUrl-----{}", htmlContent);
            helper.setText(htmlContent, true);
            helper.setFrom(from != null ? from : domainName); // Use provided sender or default domain
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email----{}", e.getMessage());
            throw new APIException("Failed to send email");
        }
    }

}
