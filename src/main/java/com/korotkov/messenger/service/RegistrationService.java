package com.korotkov.messenger.service;


import com.korotkov.messenger.model.EmailUser;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.EmailRepository;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.util.UserNotCreatedException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationService {

    UserRepository repository;

    EmailRepository emailRepository;

    PasswordEncoder passwordEncoder;

    JWTService jwtService;

    ModelMapper modelMapper;


    @Autowired
    public RegistrationService(UserRepository repository, EmailRepository emailRepository, PasswordEncoder passwordEncoder, JWTService jwtService, ModelMapper modelMapper) {
        this.repository = repository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public String register(EmailUser user) {
        String login = user.getLogin();
        if (repository.existsUserByLogin(login)) {
            throw new UserNotCreatedException("login <" + login + "> has already been taken");
        }
        String email = user.getEmail();
        if (repository.existsUserByEmail(email)) {
            throw new UserNotCreatedException("email <" + email + "> has already been taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setHideFriends(false);
        user.setIsOnlyFriends(false);
        user.setIsInBan(false);
        emailRepository.save(user);

        return jwtService.generateToken(user.getEmail(), "email");
    }

    @Transactional
    public void activate(String token, Boolean isInBan, String email) {
        String userEmail = jwtService.validateTokenAndRetrieveClaim(token, "email");
        if (isInBan == null && email == null) {

            EmailUser emailUser = emailRepository.findEmailUserByEmail(userEmail).orElseThrow();
            User map = modelMapper.map(emailUser, User.class);
            repository.save(map);
            emailRepository.delete(emailUser);
        } else if (isInBan != null) {
            User user = repository.findUserByEmail(userEmail).orElseThrow();
            user.setIsInBan(isInBan);
            repository.save(user);
        } else {
            User user = repository.findUserByEmail(userEmail).orElseThrow();
            user.setEmail(email);
            repository.save(user);
        }
    }

}
