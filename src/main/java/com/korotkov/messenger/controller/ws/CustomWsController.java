package com.korotkov.messenger.controller.ws;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.service.MessageService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CustomWsController {

    SimpMessagingTemplate messagingTemplate;

    MessageService messageService;
    ModelMapper modelMapper;
    @Autowired
    public CustomWsController(SimpMessagingTemplate messagingTemplate, MessageService messageService, ModelMapper modelMapper) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/chat") // Defines the endpoint for receiving messages
    public void sendMessage(MessageDtoRequest message) {

        sendMessage("/topic/messages/" + message.getTo(), message);
        messageService.save(message);
    }
    private void sendMessage(String destination, MessageDtoRequest message) {

        messagingTemplate.convertAndSend(
                destination,message
        );
    }
}
