package org.librarymanagementsystem.payload.request.book;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class FineDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private BigDecimal amount;
    private boolean paid;
    private LocalDate fineDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
