package com.kutuphane.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;
@Entity
@Data
@Table(name="User")
public class User {
    int id;
    String name;
    String surname;
    String password;
    String email;
    String phone;
    List <Book> books;
    User(String name, String surname, String password, String email, String phone){
        this.name=name;
        this.surname=surname;
        this.password=password;
    }

}
