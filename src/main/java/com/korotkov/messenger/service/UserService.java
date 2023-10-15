package com.korotkov.messenger.service;

import com.korotkov.messenger.model.Friend;
import com.korotkov.messenger.model.FriendRequest;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.FriendRepository;
import com.korotkov.messenger.repository.FriendRequestRepository;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.security.MyUserDetails;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    MailSenderService mailSenderService;

    FriendRepository friendRepository;
    FriendRequestRepository friendRequestRepository;

    JWTService jwtService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService, FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
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
            String token = jwtService.generateToken(userToBeUpdated.getEmail(), "email");
            mailSenderService.send(userToBeUpdated.getEmail(), "Вернуть аккаунт", "http://localhost:8080/activate?t=" + token + "&is-in-ban=" + "false");
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
            String token = jwtService.generateToken(userToBeUpdated.getEmail(), "email");
            mailSenderService.send(user.getEmail(), "Поменять почту", "http://localhost:8080/activate?t=" + token +
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

        Boolean hideFriends = user.getHideFriends();
        if (hideFriends != null) {
            userToBeUpdated.setHideFriends(hideFriends);
        }
        Boolean isOnlyFiends = user.getIsOnlyFriends();
        if (isOnlyFiends != null) {
            userToBeUpdated.setHideFriends(isOnlyFiends);
        }

        save(userToBeUpdated);
        return flag;
    }

    @Transactional
    public void acceptFriendRequest(String friendLogin, boolean isAccepted) {
        User currentUser = getCurrentUser();
        User byLogin = findByLogin(friendLogin);
        FriendRequest friendRequest = friendRequestRepository.findFriendRequestByUserFromIdAndUserToId(byLogin.getId(), currentUser.getId()).orElseThrow();

        if (isAccepted) {
            Friend entity = Friend.builder()
                    .secondUser(byLogin)
                    .firstUser(currentUser)
                    .build();
            friendRepository.save(entity);
        }

        friendRequestRepository.delete(friendRequest);

    }

    @Transactional
    public void addFriendRequest(String friendLogin) {
        User currentUser = getCurrentUser();
        User byLogin = findByLogin(friendLogin);

        FriendRequest entity = FriendRequest.builder()
                .userTo(byLogin)
                .userFrom(currentUser)
                .build();
        friendRequestRepository.save(entity);
    }

    public User getById(int id) {
        if (userRepository.existsById(id)) {
            return userRepository.getReferenceById(id);
        }
        throw new UserNotFoundException("user not found");
    }


    public List<User> getMyFriends() {
        int id = getCurrentUser().getId();
        List<User> friends = new ArrayList<>(friendRepository.getFriendsByFirstUserId(id).stream()
                .map(Friend::getSecondUser)
                .toList());
        friends.addAll(friendRepository.getFriendsBySecondUserId(id).stream()
                .map(Friend::getFirstUser).toList());
        return friends;
    }

    public List<User> getFriends(String userLogin) {
        User user = userRepository.findUserByLogin(userLogin).orElseThrow();
        if (user.getHideFriends()) {
            throw new BadCredentialsException("user hide this opportunity");
        }

        int id = getCurrentUser().getId();
        int id1 = user.getId();

        List<User> friends = friendRepository.getFriendsByFirstUserIdAndSecondUserId(id, id1).stream()
                .map(Friend::getSecondUser)
                .collect(Collectors.toList());
        friends.addAll(friendRepository.getFriendsByFirstUserIdAndSecondUserId(id1, id).stream()
                .map(Friend::getFirstUser).toList());
        return friends;
    }

    public boolean areFriends(String firstUserLogin, String secondUserLogin) {
        int id = userRepository.findUserByLogin(firstUserLogin).orElseThrow().getId();
        int id1 = userRepository.findUserByLogin(secondUserLogin).orElseThrow().getId();
        return !(friendRepository.getFriendsByFirstUserIdAndSecondUserId(id, id1).isEmpty() &&
                friendRepository.getFriendsByFirstUserIdAndSecondUserId(id1, id).isEmpty());

    }

    @Transactional
    public void delete() {
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

