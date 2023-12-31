package com.korotkov.messenger.client;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class MySessionHandler extends StompSessionHandlerAdapter {
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return MessageDtoRequest.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        MessageDtoRequest payload1 = (MessageDtoRequest) payload;
        System.out.println(payload1.getFrom() + ": " + payload1.getMessage());
    }
}
