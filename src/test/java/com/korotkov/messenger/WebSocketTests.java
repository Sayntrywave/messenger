package com.korotkov.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.messenger.config.WebSocketConfig;
import lombok.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class WebSocketTests {

    @Value("${local.server.port}")
    private int port;

    private static WebClient client;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    public void setup() throws Exception {

        StompFrameHandler runStopFrameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return null;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload);
            }
        };

        String wsUrl = "ws://127.0.0.1:" + port + WebSocketConfig.REGISTRY;

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(new MappingJackson2MessageConverter(), new StringMessageConverter())));

        MySessionHandler sessionHandler = new MySessionHandler();
        StompSession stompSession = stompClient.connectAsync(wsUrl, sessionHandler).get();

//        StompSession stompSession = stompClient.connectAsync(wsUrl, new StompSessionHandlerAdapter() {
//                })
//                .get(1, TimeUnit.SECONDS);

        client = WebClient.builder()
                .stompClient(stompClient)
                .stompSession(stompSession)
                .handler(runStopFrameHandler)
                .build();


//        stompSession.subscribe("/id/messages",client.getHandler());


    }

    @AfterAll
    public void tearDown() {

        if (client.getStompSession().isConnected()) {
            client.getStompSession().disconnect();
            client.getStompClient().stop();
        }
    }

    @SneakyThrows
    @Test
    public void should_PassSuccessfully_When_CreateChat() {
        StompSession stompSession = client.getStompSession();


        StompFrameHandler handler = client.getHandler();
        String chatName = "crazy chat";

        String wsUrl = "ws://127.0.0.1:" + port + WebSocketConfig.REGISTRY;

        stompSession.send("/chat", "123");


//        StompSession stompSession = client.getStompSession();
//
//        RunStopFrameHandler handler = client.getHandler();
//
//        String chatName = "Crazy chat";
//
//        stompSession.send(ChatWsController.CREATE_CHAT, chatName);
//
//        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(ChatRestController.FETCH_CHATS)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
//
//        List<LinkedHashMap<String, Object>> params = (List<LinkedHashMap<String, Object>>) mapper.readValue(contentAsString, List.class);
//
//        Assertions.assertFalse(params.isEmpty());
//
//        String chatId = (String) params.get(0).get("id");
//
//        String destination = ChatWsController.getFetchPersonalMessagesDestination(chatId, RandomIdGenerator.generate());
//
//        final RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());
//        stompSession.subscribe(destination, runStopFrameHandler);
    }

    private List<Transport> createTransportClient() {

        List<Transport> transports = new ArrayList<>(1);

        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }

    @Data
    @AllArgsConstructor
    private class RunStopFrameHandler implements StompFrameHandler {

        CompletableFuture<Object> future;

        @Override
        public @NonNull Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {

            System.out.println("я тут");
            for (int i = 0; i < 22222222; i++) {
                System.out.println(o);

            }
            System.out.println(stompHeaders);
            future.complete(o);
            future = new CompletableFuture<>();
        }
    }

    @Data
    @Builder
    private static class WebClient {

        WebSocketStompClient stompClient;

        StompSession stompSession;

        String sessionToken;

        StompFrameHandler handler;
    }
}

