package com.korotkov.messenger.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageResponse {
    String nicknameFrom;
    String nicknameTo;
    String message;
}
