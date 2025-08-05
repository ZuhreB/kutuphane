package com.kutuphane.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Borrows")
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowID;

    // bir kullanıcı birden fazla kitap alabildiği için
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    // bir kitap birden fazla kez alına bileceği için çünkü tek kopyası olmak zorunda değil
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID", nullable = false)
    private Book book;

    @Column(name = "BorrowDate", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "ReturnDate")
    private LocalDateTime returnDate;

    @Column(name = "ActualReturnDate")
    private LocalDateTime actualReturnDate;

    @Column(name = "Status", nullable = false, length = 20)
    private String status; // teslim tarihi gecikmiş olabilir gibi şeyleri tutmak içim kullanıcam

    @Column(name = "FineAmount", precision = 10, scale = 2)
    private BigDecimal fineAmount; // eğer teslim tarihi geçtiyse ceza olarak ödenecek para
}