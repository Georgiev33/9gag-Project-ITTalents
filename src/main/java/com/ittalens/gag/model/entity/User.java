package com.ittalens.gag.model.entity;

import lombok.Data;
import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private int age;
    @Column
    private String userName;
    @Column
    private String email;
    @Column
    private String password;
    @Column(name = "created_at")
    private LocalDateTime registerDate;
    @Column
    private boolean isActive;
}

