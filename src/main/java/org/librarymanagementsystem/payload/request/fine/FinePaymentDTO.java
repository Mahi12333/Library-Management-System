package org.librarymanagementsystem.payload.request.fine;

public class FinePaymentDTO {
    private Long fineId;        // ID of the fine being paid
    private Long userId;        // ID of the user paying the fine
    private Double amountPaid;
}
