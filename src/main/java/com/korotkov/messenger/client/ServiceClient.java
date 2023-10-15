package com.korotkov.messenger.client;

import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Scanner;

public class ServiceClient {
    public static void main(String... argv) {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(new MappingJackson2MessageConverter(), new StringMessageConverter())));
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MySessionHandler();
        stompClient.connectAsync(url, sessionHandler);

        new Scanner(System.in).nextLine(); //Don't close immediately.
    }
}
