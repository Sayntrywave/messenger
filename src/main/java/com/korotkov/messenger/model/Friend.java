package com.korotkov.messenger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friends")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "first_id", referencedColumnName = "id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_id", referencedColumnName = "id")
    private User secondUser;
}