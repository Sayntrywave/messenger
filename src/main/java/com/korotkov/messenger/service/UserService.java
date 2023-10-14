package com.korotkov.messenger.service;

import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.security.MyUserDetails;
import com.korotkov.messenger.util.UserHasNoRightsException;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByLogin(String login) {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UserNotFoundException("user not found"));
    }


    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public boolean update(User user) {

        boolean flag = false;
        int id = getCurrentUser().getId();

        User userToBeUpdated = getById(id);

        Boolean isInBan = user.getIsInBan();
        if (isInBan != null) {
            userToBeUpdated.setIsInBan(isInBan);
        }

        String name = user.getName();
        if (name != null && !name.isEmpty()) {
            userToBeUpdated.setName(name);
        }
        String email = user.getEmail();
        if (email != null && !email.isEmpty()) {
            userToBeUpdated.setEmail(email);
        }

        String login = user.getLogin();
        if (login != null && !login.isEmpty()) {
            if (userRepository.existsUserByLogin(login)) {
                throw new BadCredentialsException("login <" + login + "> has already been taken");
            }
            flag = true;
            userToBeUpdated.setLogin(login);
        }
        String password = user.getPassword();
        if (password != null && !password.isEmpty()) {
            userToBeUpdated.setPassword(passwordEncoder.encode(password));
        }

        save(userToBeUpdated);
        return flag;
    }

    public User getById(int id) {
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        }
        throw new UserNotFoundException("user not found");
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((MyUserDetails) principal).user();
        }
        return new User();
    }
}

