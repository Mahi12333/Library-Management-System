package org.librarymanagementsystem.services;



public interface NewsletterService {
    String subscribe(String email);
    String unsubscribe( String email, String token);
}
