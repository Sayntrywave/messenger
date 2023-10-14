package com.korotkov.messenger.service;


import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.util.UserNotCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user) {
        String login = user.getLogin();
        if (repository.existsUserByLogin(login)) {
            throw new UserNotCreatedException("login <" + login + "> has already been taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsInBan(false);
        repository.save(user);

    }

}
