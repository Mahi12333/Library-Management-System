package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.NotificationStatus;
import org.librarymanagementsystem.model.Notification;
import org.librarymanagementsystem.repository.NotificationRepository;
import org.librarymanagementsystem.services.EmailServices;
import org.librarymanagementsystem.utils.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServicesImp implements EmailServices {
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;


    @Async
    public void sendEmail(String to, String body, String subject) {
        emailService.sendEmail(to, body, subject, null);
    }


    @Override
    @Async
    public void send(String to, String email, String subject, Notification notification) {
        try {
            log.info("notification-- {}", notification);
            sendEmail(to, email, subject); // Reuse sendEmail method for notifications

            // Update notification status to SENT
            notification.setNotificationStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Failed to send notification email", e);
            // Update notification status to FAILED
            notification.setNotificationStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);

            throw new IllegalStateException("Failed to send notification email", e);
        }
    }
}
