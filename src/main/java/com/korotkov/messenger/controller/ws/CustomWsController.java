package com.korotkov.messenger.controller.ws;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomWsController {

    SimpMessagingTemplate messagingTemplate;

    UserService userService;

    @Autowired
    public CustomWsController(SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/chat") // Defines the endpoint for receiving messages
    public void sendMessage(MessageDtoRequest message) {

        // Handle the incoming message and create a response
        System.out.println("ОТПРАВИЛ");

//        String nickname = userService.getCurrentUser().getLogin();

//        message.setNickname(nickname);

        sendMessage("/topic/messages/" + message.getNickname(), message);

    }
    private void sendMessage(String destination, MessageDtoRequest message) {

        messagingTemplate.convertAndSend(
                destination,message
        );
    }
}
