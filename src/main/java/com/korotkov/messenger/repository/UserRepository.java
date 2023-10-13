package com.korotkov.messenger.repository;


import com.korotkov.messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByLogin(String username);

    boolean existsUserByLogin(String login);
}
