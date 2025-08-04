package com.kutuphane.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="book")
public class Book {
    String barcode;
    String title;
    String year;
    String writer;
    String topic;

}
