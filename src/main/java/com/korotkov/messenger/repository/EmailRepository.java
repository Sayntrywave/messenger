package com.korotkov.messenger.repository;

import com.korotkov.messenger.model.EmailUser;
import com.korotkov.messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailUser, Integer> {
    Optional<EmailUser> findEmailUserByEmail(String email);
}
