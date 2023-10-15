package com.korotkov.messenger.controller.ws;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomWsController {

    SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CustomWsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat") // Defines the endpoint for receiving messages
    public void sendMessage(MessageDtoRequest message) {
        // Handle the incoming message and create a response
        System.out.println("ОТПРАВИЛ");
        sendMessage("/topic/messages/" + message.getNickname(), message);

    }
    private void sendMessage(String destination, MessageDtoRequest message) {

        messagingTemplate.convertAndSend(
                destination,message
        );
    }
}
