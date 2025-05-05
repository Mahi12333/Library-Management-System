package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.ContactusStatus;
import org.librarymanagementsystem.emun.NotificationStatus;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.model.ContactUs;
import org.librarymanagementsystem.model.Newsletter;
import org.librarymanagementsystem.model.Notification;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.payload.request.ContactUsDTO;
import org.librarymanagementsystem.repository.ContactUsRepository;
import org.librarymanagementsystem.repository.NewsletterRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.services.NewsletterService;
import org.librarymanagementsystem.utils.AuthUtil;
import org.librarymanagementsystem.utils.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterServiceImp implements NewsletterService {
    private final NewsletterRepository newsletterRepository;
    private final EmailService emailService;
    private final AuthUtil authUtil;
    private final ContactUsRepository contactUsRepository;
    private final UserRepository userRepository;

    @Override
    public String subscribe(String email) {
        if(!isValidEmail(email)){
            throw new APIException("Invalid email format.");
        }
        Optional<Newsletter> optionalNewsletter = newsletterRepository.findByEmail(email);
        if(optionalNewsletter.isPresent()){
            Newsletter subscribe = optionalNewsletter.get();
            if(!subscribe.isActive()){
               subscribe.setActive(true);
               subscribe.setUnsubscribeToken(UUID.randomUUID().toString());
               newsletterRepository.save(subscribe);
               sendSubscriptionEmail(email, subscribe.getUnsubscribeToken());
                return "You have successfully re-subscribed!";
            }
            return "Email is already subscribed.";
        }
        Newsletter newSubscribe = new Newsletter();
        newSubscribe.setEmail(email);
        newSubscribe.setUnsubscribeToken(UUID.randomUUID().toString());
        newsletterRepository.save(newSubscribe);
        sendSubscriptionEmail(email, newSubscribe.getUnsubscribeToken());
        return "You have successfully subscribed!";
    }

    @Override
    public String unsubscribe(String email, String token) {
        if(!isValidEmail(email)){
            throw new APIException("Invalid email format.");
        }
        Optional<Newsletter> optionalNewsletter = newsletterRepository.findByEmail(email);
        if(optionalNewsletter.isEmpty()){
            throw new APIException("User not Register!");
        }
        Newsletter unsubscribe = optionalNewsletter.get();
        if(!unsubscribe.getUnsubscribeToken().matches(token)){
            throw new APIException("Invalid or expired token.");
        }
        if(!unsubscribe.isActive()) return "Invalid or expired token.";
        unsubscribe.setActive(false);
        newsletterRepository.save(unsubscribe);
        sendUnsubscribeEmail(unsubscribe.getEmail());
        return "You have successfully unsubscribed!";
    }

    @Override
    public String contactUs(ContactUsDTO request) {
        String subject = request.getSubject() ;
        String body = request.getMessage();
        User userId = null;
        try{
            userId = authUtil.loggedInUser();
        } catch (Exception e) {
         log.error("Contact UserId---");
        }
        ContactUs contactEntity = new ContactUs();
        contactEntity.setEmail(request.getEmail());
        contactEntity.setFullname(request.getFullname());
        contactEntity.setSubject(subject);
        contactEntity.setMessage(body);
        contactEntity.setSentDate(new Timestamp(System.currentTimeMillis()));
        if (userId != null) {
            userRepository.findById(userId.getId()).ifPresent(contactEntity::setUser);
        }

       try{
           contactEmail(request.getEmail(), body, subject, contactEntity);
           return "Your concerns successfully register!.";
       } catch (Exception e) {
           log.error("contactEntity Exception--- {}", e.getMessage());
           return "Your concerns was not successfully registered.";
       }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private void sendSubscriptionEmail(String email, String token) {
        String subject = "Welcome to Our Newsletter!";
        String body = "Thank you for subscribing! " +
                "To unsubscribe, click the link:\n" +
                "http://localhost:8080/api/newsletter/unsubscribe?token=" + token;

        emailService.sendEmail(email, body, subject, null); // No need to change this line
    }

    private void sendUnsubscribeEmail(String email) {
        String subject = "You have been unsubscribed";
        String body = "You have successfully unsubscribed. If this was a mistake, you can re-subscribe.";

        emailService.sendEmail(email, body, subject, null); // No need to change this line
    }


    @Async
    public void contactEmail(String email, String body, String subject, ContactUs contactUs) {
        try {
            log.info("contactUs-- {}", contactUs);
            emailService.sendEmail(email, body, subject, null);

            // Update notification status to SENT
            contactUs.setContactusStatus(ContactusStatus.SENT);
            contactUsRepository.save(contactUs);
        } catch (Exception e) {
            log.error("Failed to send contact us email", e);
            // Update notification status to FAILED
            contactUs.setContactusStatus(ContactusStatus.FAILED);
            contactUsRepository.save(contactUs);

            throw new IllegalStateException("Failed to send cantact us email", e);
        }
    }
}
