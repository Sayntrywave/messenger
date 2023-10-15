package com.korotkov.messenger.client;

import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class ServiceClient {
    public static void main(String... argv) throws URISyntaxException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(new MappingJackson2MessageConverter(), new StringMessageConverter())));
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        StompHeaders headers = new StompHeaders();
        System.out.println(headers.get("Authorization"));

        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MySessionHandler();
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJpc3MiOiJuaWtpdGEiLCJleHAiOjE2OTczNTgwMzksImlhdCI6MTY5NzM1NDQzOSwidXNlcm5hbWUiOiJuIn0." +
                "eKxEF2iMQ1rJn0aIwKMl5py3pn3M3S2NGQ3dyJL97fo");
        URI uri = new URI(url);
        stompClient.connectAsync(uri,webSocketHttpHeaders,headers,sessionHandler);

        new Scanner(System.in).nextLine(); //Don't close immediately.
    }
}
