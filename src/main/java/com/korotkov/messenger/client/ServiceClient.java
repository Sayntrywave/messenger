package com.korotkov.messenger.client;

import com.korotkov.messenger.dto.request.MessageDtoRequest;
import com.korotkov.messenger.dto.response.LoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ServiceClient {
    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(new MappingJackson2MessageConverter(), new StringMessageConverter())));
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        StompHeaders headers = new StompHeaders();

        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MySessionHandler();

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();

        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, String> map = new HashMap<>();
        String nickname = "nikitos";
        map.put("login", nickname);
        map.put("password","qwerty");

        HttpEntity<Object> objectHttpEntity = new HttpEntity<>(map);

        String token = restTemplate.postForEntity("http://localhost:8080/login",objectHttpEntity,LoginResponse.class).getBody().getToken();;

//        assert strings != null;
        webSocketHttpHeaders.add("Authorization",token);
        URI uri = new URI(url);
        StompSession stompSession = stompClient.connectAsync(uri, webSocketHttpHeaders, headers, sessionHandler).get();

        stompSession.subscribe("/topic/messages/nikitos", sessionHandler);
        MessageDtoRequest messageDtoRequest = new MessageDtoRequest(nickname,"i'm here");
        stompSession.send("/chat", messageDtoRequest);

        new Scanner(System.in).nextLine(); //Don't close immediately.
    }
}
