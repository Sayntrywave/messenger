package com.korotkov.messenger.service;

import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {

    UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByLogin(String login) {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UserNotFoundException("user not found"));
    }
}

