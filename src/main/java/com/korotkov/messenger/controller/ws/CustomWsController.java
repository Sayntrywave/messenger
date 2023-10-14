package com.korotkov.messenger.controller.ws;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomWsController {

    SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CustomWsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat") // Defines the endpoint for receiving messages
    public String sendMessage(Message message) {
        // Handle the incoming message and create a response

        sendMessage("/topic/messages","","1234");

        System.out.println("ОТПРАВИЛ");
        return "1234";
    }

    private void sendMessage(String destination, String sessionId, String message) {
        messagingTemplate.convertAndSend(
                destination,message
        );
    }
}
