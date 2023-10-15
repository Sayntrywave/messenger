package com.korotkov.messenger.service;

import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.EmailRepository;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.security.MyUserDetails;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    MailSenderService mailSenderService;

    EmailRepository emailRepository;

    ModelMapper modelMapper;

    JWTService jwtService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService, EmailRepository emailRepository, ModelMapper modelMapper, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
        this.emailRepository = emailRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }


    public User findByLogin(String login) {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UserNotFoundException("user not found"));
    }
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user not found"));
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
            String token = jwtService.generateToken(userToBeUpdated.getEmail(),"email");
            mailSenderService.send(userToBeUpdated.getEmail(),"Вернуть аккаунт","http://localhost:8080/activate?t=" + token + "&is-in-ban=" + "false");
        }

        //todo send to email activate link

        String name = user.getName();
        if (name != null && !name.isEmpty()) {
            userToBeUpdated.setName(name);
        }
        String email = user.getEmail();
        if (email != null && !email.isEmpty()) {
            if (userRepository.existsUserByLogin(email)) {
                throw new BadCredentialsException("email <" + email + "> has already been taken");
            }
            String token = jwtService.generateToken(userToBeUpdated.getEmail(),"email");
            mailSenderService.send(user.getEmail(),"Поменять почту","http://localhost:8080/activate?t=" + token +
                    "&email=" + user.getEmail());

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

    @Transactional
    public void delete(){
        User currentUser = getCurrentUser();
        userRepository.delete(currentUser);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((MyUserDetails) principal).user();
        }
        return new User();
    }
}

