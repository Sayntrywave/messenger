package com.korotkov.messenger.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@ToString
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "surname")
    private String surname;


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

    @Column(name = "is_only_friends")
    private Boolean isOnlyFriends;

    @Column(name = "hide_friends")
    private Boolean hideFriends;

    public void setOnlyFriends(Boolean onlyFriends) {
        isOnlyFriends = onlyFriends;
    }

    public void setHideFriends(Boolean hideFriends) {
        this.hideFriends = hideFriends;
    }
}
