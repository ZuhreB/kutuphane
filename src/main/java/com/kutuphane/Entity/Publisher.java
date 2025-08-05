package com.kutuphane.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherID;

    @Column(name = "PublisherName", unique = true, nullable = false, length = 100)
    private String publisherName;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "Email", length = 100)
    private String email;

}