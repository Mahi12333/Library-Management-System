package org.librarymanagementsystem.services;


import jakarta.validation.Valid;
import org.librarymanagementsystem.payload.request.ContactUsDTO;

public interface NewsletterService {
    String subscribe(String email);
    String unsubscribe( String email, String token);
    String contactUs(ContactUsDTO request);
}
