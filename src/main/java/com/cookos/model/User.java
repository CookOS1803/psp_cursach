package com.cookos.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private int id;

    @Column (name = "login")
    private String login;
    
    @Column (name = "password")
    private byte[] password;

    public User() {
    }

    public User(int id, String login, byte[] password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }    
}
