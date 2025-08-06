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
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

    @Column(name = "Username", unique = true, nullable = false, length = 50)
    private String username; //kullanıcı adı olarak

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "Role", nullable = false, length = 20)
    private String role; //admin ya da user
}