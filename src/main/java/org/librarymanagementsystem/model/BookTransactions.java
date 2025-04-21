//package org.librarymanagementsystem.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import org.librarymanagementsystem.emun.TransactionType;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//@Entity
//@Setter
//@Getter
//@Table(name = "book_transactions")
//@AllArgsConstructor
//@NoArgsConstructor
//public class BookTransactions {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Book book;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TransactionType transactionType;
//
//    @CreationTimestamp
//    private Timestamp transactionTime;
//
//    @CreationTimestamp
//    private LocalDateTime createAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updateAt;
//}
