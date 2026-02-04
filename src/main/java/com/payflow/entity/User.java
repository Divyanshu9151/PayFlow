package com.payflow.entity;



import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private Date createdAt;

}
