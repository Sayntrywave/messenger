package com.korotkov.messenger.repository;

import com.korotkov.messenger.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> getTokenByToken(String token);
}
