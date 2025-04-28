package org.librarymanagementsystem.payload.request.fine;

import lombok.Builder;
import lombok.Getter;

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
