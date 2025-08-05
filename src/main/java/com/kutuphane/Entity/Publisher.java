package com.kutuphane.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
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