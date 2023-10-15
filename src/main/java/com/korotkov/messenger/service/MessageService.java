package com.korotkov.messenger.service;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.model.Message;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.repository.MessageRepository;
import com.korotkov.messenger.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MessageService {

    MessageRepository messageRepository;
    UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> getMessagesWith(int userId, int userFromId){
        List<Message> messagesByUserToIdAndUserFromId = messageRepository.getMessagesByUserToIdAndUserFromId(userId, userFromId);
        List<Message> messagesByUserToIdAndUserFromId1 = messageRepository.getMessagesByUserToIdAndUserFromId(userFromId, userId);
        messagesByUserToIdAndUserFromId.addAll(messagesByUserToIdAndUserFromId1);
        return messagesByUserToIdAndUserFromId;
    }

    @Transactional
    public void save(MessageDtoRequest message){
        Message message1 = new Message();
        User userFrom = userRepository.findUserByLogin(message.getFrom()).orElseThrow();
        User userTo = userRepository.findUserByLogin(message.getTo()).orElseThrow();

        message1.setUserFrom(userFrom);
        message1.setUserTo(userTo);

        message1.setMessage(message.getMessage());
        save(message1);
    }

    @Transactional
    public void save(Message message){
        messageRepository.save(message);
    }
}
