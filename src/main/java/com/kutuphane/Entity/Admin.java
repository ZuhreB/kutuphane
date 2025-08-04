package com.kutuphane.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="Admin")
public class Admin {
    int ıd;
    String name;
    String surname;
    String password;
}
