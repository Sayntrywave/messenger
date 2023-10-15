package com.korotkov.messenger.controller.ws;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.service.MessageService;
import com.korotkov.messenger.util.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessengerWsController {

    SimpMessagingTemplate messagingTemplate;

    MessageService messageService;

    @Autowired
    public MessengerWsController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat")
    public void sendMessage(MessageDtoRequest message) {

        try {
            messageService.save(message);
            sendMessage("/topic/messages/" + message.getTo(), message);
        } catch (UserNotFoundException | BadCredentialsException e) {
            message.setMessage(e.getMessage());
            message.setTo(message.getFrom());
            message.setFrom("server");
            sendMessage("/topic/messages/" + message.getTo(), message);
        }
    }

    private void sendMessage(String destination, MessageDtoRequest message) {

        messagingTemplate.convertAndSend(
                destination, message
        );
    }
}
