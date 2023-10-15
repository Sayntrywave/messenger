package com.korotkov.messenger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friends_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_from_id", referencedColumnName = "id")
    private User userFrom;

    @ManyToOne
    @JoinColumn(name = "user_to_id", referencedColumnName = "id")
    private User userTo;
}
