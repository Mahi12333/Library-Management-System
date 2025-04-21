package org.librarymanagementsystem.services;

import org.librarymanagementsystem.model.Borrowed_book_records;

public interface NotificationService {
    void borrowBookNotification(Borrowed_book_records savedRecord);
    void reminderNotification(Borrowed_book_records savedRecord);
    void fineImposedNotification(Borrowed_book_records borrowedBook);
    void bookReturnedNotification(Borrowed_book_records borrowedBook);
}
