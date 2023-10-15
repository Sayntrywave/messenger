package com.korotkov.messenger.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_to_be_confirmed")
@Data
@NoArgsConstructor
@ToString
@Getter
@Setter
public class EmailUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "is_in_ban")
    private Boolean isInBan;
}
