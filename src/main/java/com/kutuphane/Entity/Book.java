package com.kutuphane.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookID;

    @Column(name = "Title", nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY) //bir yazar birden fazla kitaba sahip olabilir
    @JoinColumn(name = "AuthorID", nullable = false)
    private com.kutuphane.Entity.Author author;

    @ManyToOne(fetch = FetchType.LAZY)// bir yayıncı birden fazla kitap yayınlamış olabileceği için
    @JoinColumn(name = "PublisherID", nullable = false)
    private com.kutuphane.Entity.Publisher publisher;

    @Column(name = "ISBN", unique = true, nullable = false, length = 20)
    private String isbn; //barkodu

    @Column(name = "PublicationYear")
    private Integer publicationYear;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description; // kitabın kısa bir özeti

    @Column(name = "TotalCopies", nullable = false)
    private Integer totalCopies; // kütüphanede aynı kitaptan birden fazla olabileceği için

    @Column(name = "AvailableCopies", nullable = false)
    private Integer availableCopies; // eğer kitaptan alan varsa aynı kitaptan kalan kitap sayısını göstermek için

    @Column(name = "AddedDate") // admin ekleme tarihinden kitap aramak isterse veya kaç kitap eklenmiş gibi farklı şeylere bakılabilmesi için
    private LocalDateTime addedDate;

    @Column(name ="Topic",nullable=true)
    private String topic;

}