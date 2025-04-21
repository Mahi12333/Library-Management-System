package org.librarymanagementsystem.payload.request.book;

public class FinePaymentDTO {
    private Long fineId;        // ID of the fine being paid
    private Long userId;        // ID of the user paying the fine
    private Double amountPaid;
}
