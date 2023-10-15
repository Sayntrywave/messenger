package com.korotkov.messenger;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;


public class MySessionHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/messages", this);
        System.out.println(321);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println(123);
        exception.printStackTrace();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        System.out.println(123);
        return MessageDtoRequest.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

        System.out.println("Я ПОЛУЧИЛ");
        System.out.println(payload);
    }
}