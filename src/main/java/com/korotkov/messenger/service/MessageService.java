package com.korotkov.messenger.service;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.model.Message;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.MessageRepository;
import com.korotkov.messenger.repository.UserRepository;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MessageService {

    MessageRepository messageRepository;
    UserRepository userRepository;

    UserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Message> getMessagesWith(int userId, int userFromId) {
        List<Message> messagesByUserToIdAndUserFromId = messageRepository.getMessagesByUserToIdAndUserFromId(userId, userFromId);
        List<Message> messagesByUserToIdAndUserFromId1 = messageRepository.getMessagesByUserToIdAndUserFromId(userFromId, userId);
        messagesByUserToIdAndUserFromId.addAll(messagesByUserToIdAndUserFromId1);
        return messagesByUserToIdAndUserFromId;
    }

    @Transactional
    public void save(MessageDtoRequest message) {
        Message message1 = new Message();
        String from = message.getFrom();
        User userFrom = userRepository.findUserByLogin(from).orElseThrow(() -> new UserNotFoundException("can't find user " + from));
        String to = message.getTo();
        User userTo = userRepository.findUserByLogin(to).orElseThrow(() -> new UserNotFoundException("can't find user " + to));


        if (userTo.getIsOnlyFriends() && !userService.areFriends(userFrom.getLogin(),userTo.getLogin())) {
            throw new BadCredentialsException("this user can receive messages only from friends");
        }

        message1.setUserFrom(userFrom);
        message1.setUserTo(userTo);

        message1.setMessage(message.getMessage());
        save(message1);
    }

    @Transactional
    public void save(Message message) {
        messageRepository.save(message);
    }
}
