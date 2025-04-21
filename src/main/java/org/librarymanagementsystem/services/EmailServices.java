package org.librarymanagementsystem.services;

import org.librarymanagementsystem.model.Notification;

public interface EmailServices {
    void send(String to, String body, String subject, Notification notification);
}
