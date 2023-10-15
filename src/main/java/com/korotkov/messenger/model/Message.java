package com.korotkov.messenger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_from_id",referencedColumnName = "id")
    private User userFrom;

    @ManyToOne
    @JoinColumn(name = "user_to_id",referencedColumnName = "id")
    private User userTo;

    @Column(name = "message")
    private String message;

}
