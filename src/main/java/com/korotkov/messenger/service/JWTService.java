package com.korotkov.messenger.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.korotkov.messenger.model.Token;
import com.korotkov.messenger.repository.TokenRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class JWTService {

    String secret;
    JWTVerifier verifier;

    TokenRepository tokenRepository;

    public JWTService(@Value("${jwt.secret}") String secret, TokenRepository tokenRepository) {
        this.secret = secret;
        this.verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("nikita")
                .build();
        this.tokenRepository = tokenRepository;
    }


    public String generateToken(String username) {
        return generateToken(username,"username",60);
    }

    private String generateToken(String claim, String claimName, int expireIn) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(expireIn).toInstant());


/* String sign = JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("nikita")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
        tokenRepository.save(Token.builder()
                .token(sign)
                .isExpired(false)
                .build());*/
        String sign = JWT.create()
                .withSubject("User details")
                .withClaim(claimName, claim)
                .withIssuedAt(new Date())
                .withIssuer("nikita")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
        tokenRepository.save(Token.builder()
                .token(sign)
                .isExpired(false)
                .build());
        return sign;
    }

    public String validateTokenAndRetrieveClaim(String stringToken){
        Token token = tokenRepository.getTokenByToken(stringToken).orElseThrow(() -> new JWTVerificationException("такого токена не существует"));
        DecodedJWT jwt = verifier.verify(stringToken);
        if(token.getIsExpired()){
            throw new TokenExpiredException("Токен невалидный", jwt.getExpiresAt().toInstant());
        }

        return jwt.getClaim("username").asString();
    }

    public void invalidateToken(String stringToken){
        Token token = tokenRepository.getTokenByToken(stringToken).orElseThrow();
        token.setIsExpired(true);
        tokenRepository.save(token);
    }
}