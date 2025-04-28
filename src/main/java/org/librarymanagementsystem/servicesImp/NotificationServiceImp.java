package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.NotificationType;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.exception.ResourceNotFoundException;
import org.librarymanagementsystem.model.Borrowed_book_records;
import org.librarymanagementsystem.model.Notification;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.repository.NotificationRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.services.EmailServices;
import org.librarymanagementsystem.services.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailServices emailServices;
    private final UserRepository userRepository;


    @Async
    @Override
    public void borrowBookNotification(Borrowed_book_records savedRecord) {
        Date borrowedAt = savedRecord.getBorrowedAt();
        Date dueDate = savedRecord.getDueDate();
        if (borrowedAt == null || dueDate == null) {
            throw new APIException("Borrowed or Due Date is missing");
        }
        Notification notification = new Notification();
        notification.setUser(savedRecord.getUser());
        String borrowedDateStr = LocalDateTime.ofInstant(borrowedAt.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        LocalDate localDueDate = Instant.ofEpochMilli(dueDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String dueDateStr = localDueDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String message = "Congratulations! 🎉 You have successfully borrowed '" +
                savedRecord.getBook().getTitle() + "' on " + borrowedDateStr +
                ".<br><br>You now have 15 days to enjoy reading it. We kindly request that you return it to us on or before " +
                dueDateStr + " to avoid any late fees 📆, which are ₹10 per day for late returns.<br><br>If you need to renew the book or have any questions, please don't hesitate to reach out to us.<br><br>Thank you for choosing our library!";
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.BORROW);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
        sendNotification(notification);
    }

    @Async
    @Override
    public void reminderNotification(Borrowed_book_records savedRecord) {
        Notification notification = new Notification();
        notification.setUser(savedRecord.getUser());
        Date dueDate = savedRecord.getDueDate();
        LocalDate localDueDate = Instant.ofEpochMilli(dueDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String dueDateStr = localDueDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String message = "This is a friendly reminder that the due date to return '" +
                savedRecord.getBook().getTitle() + "' is approaching. Please ensure that you return the book by " +

                dueDateStr +
                " to avoid any late fees. 📅" +
                "<br><br>If you need more time, consider renewing your book through our online portal or by contacting us." +
                "<br><br>Thank you, and happy reading! 😊";

        notification.setMessage(message);
        notification.setNotificationType(NotificationType.REMINDER);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
        sendNotification(notification);
    }

    @Override
    public void fineImposedNotification(Borrowed_book_records borrowedBook) {
        Notification notification = new Notification();
        notification.setUser(borrowedBook.getUser());
        notification.setMessage("We hope you enjoyed reading '" +
                borrowedBook.getBook().getTitle() +
                "'. Unfortunately, our records show that the book was returned after the due date of " +
                LocalDateTime.ofInstant(borrowedBook.getDueDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")) +
                ". As a result, a fine of ₹10 per day has been imposed for the late return.<br><br>The total fine amount for this overdue return is ₹" +
                borrowedBook.getFine().getAmount() +
                ".<br><br>If you have any questions or would like to discuss this matter further, please don't hesitate to contact us.<br><br>Thank you for your understanding and for being a valued member of our library.");
        notification.setNotificationType(NotificationType.FINE);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));

        notificationRepository.save(notification);
        sendNotification(notification);
    }

    @Override
    public void bookReturnedNotification(Borrowed_book_records borrowedBook) {
        Notification notification = new Notification();
        notification.setUser(borrowedBook.getUser());
        Date borrowedAt = borrowedBook.getBorrowedAt();
        String borrowedDateStr = LocalDateTime.ofInstant(borrowedAt.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"));
        notification.setMessage("Thank you for returning '" + borrowedBook.getBook().getTitle() + "' book on " + borrowedDateStr + ". We hope you enjoyed the book!" +
                "<br><br>Feel free to explore our collection for your next read. If you have any questions or need assistance, we’re here to help." +
                "<br><br>Thank you for choosing LibraryMan!");
        notification.setNotificationType(NotificationType.RETURNED);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);
        sendNotification(notification);
    }


    public void accountCreatedNotification(User members) {
        Notification notification = new Notification();
        notification.setUser(members);
        notification.setMessage("We’re excited to welcome you to LibraryMan! Your account has been successfully created, and you’re now part of our community of book lovers. 📚<br><br>Feel free to explore our vast collection of books and other resources. If you have any questions or need assistance, our team is here to help.<br><br>Happy reading! 📖");
        notification.setNotificationType(NotificationType.ACCOUNT_CREATED);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));

        notificationRepository.save(notification);
        sendNotification(notification);
    }


    public void accountDeletionNotification(User members) {
        Notification notification = new Notification();
        notification.setUser(members);
        notification.setMessage("We’re sorry to see you go! Your account with LibraryMan has been successfully deleted as per your request.<br><br>If you change your mind in the future, you’re always welcome to create a new account with us. Should you have any questions or concerns, please don’t hesitate to reach out.<br><br>Thank you for being a part of our community.");
        notification.setNotificationType(NotificationType.ACCOUNT_DELETED);
        notification.setSentDate(new Timestamp(System.currentTimeMillis()));
        sendNotification(notification);
    }


    private void sendNotification(Notification notification) {
        log.info("notification--{}",notification);
        emailServices.send(
                notification.getUser().getEmail(),
                subject(notification.getNotificationType()),
                buildEmail(
                        subject(notification.getNotificationType()),
                        notification.getUser().getUserName(),
                        notification.getMessage()
                ),
                notification
        );
    }

    /*private String buildEmail(NotificationType notificationType, String memberName, String notificationMessage) {
        return "Hello " + memberName + ",<br><br>" + notificationMessage + "<br><br>" + "Best regards,<br>The LibraryMan Team";
    }*/

    private String subject(NotificationType notificationType) {
        switch (notificationType) {
            case ACCOUNT_CREATED:
                return "Welcome to LibraryMan!";
            case ACCOUNT_DELETED:
                return "Your LibraryMan Account has been Deleted";
            case BORROW:
                return "Book Borrowed Successfully";
            case REMINDER:
                return "Reminder: Book Due Date Approaching";
            case PAID:
                return "Payment Received";
            case FINE:
                return "Fine Imposed for Late Return";
            case RETURNED:
                return "Thank You for Returning Your Book";
            case UPDATE:
                return "Your Account Details Have Been Updated";
            default:
                return "LibraryMan Notification";
        }
    }

    private String buildEmail(String notificationType, String memberName, String notificationMessage) {

        return "<div style=\"font-family:Helvetica,Arial,sans-serif; font-size:16px; margin:0; color:#0b0c0c; background-color:#ffffff\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + notificationType +
                "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + memberName + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" + notificationMessage + "</p>" +
                "<p>Best regards,</p>" +
                "<p>LibraryMan</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


    // Scheduled method to send reminders for books due in 2 days
    @Scheduled(cron = "0 24 17 * * ?") // Runs every day at 5:14 PM // Runs every day at 4:23 PM
    public void sendDueDateReminders() {
        Calendar calendar = Calendar.getInstance();

        // Get today's date
        Date today = calendar.getTime();

        // Move to two days from now
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date twoDaysFromNow = calendar.getTime();

        // Fetch borrowings due soon
        List<Borrowed_book_records> borrowingsDueSoon = notificationRepository.findBorrowingsDueInDays(today, twoDaysFromNow);
        log.info("borrowingsDueSoon--{}", borrowingsDueSoon);
        // Send reminders for each borrowing
        for (Borrowed_book_records borrowing : borrowingsDueSoon) {
            try {
                Optional<User> member = userRepository.findById(borrowing.getUser().getId());
                log.info("member--{}", member);
                if (member.isPresent())
                    reminderNotification(borrowing);
            } catch (ResourceNotFoundException e) {
                log.error("Member not found for memberId: " + borrowing.getUser().getId(), e);
            }
        }
    }




}
