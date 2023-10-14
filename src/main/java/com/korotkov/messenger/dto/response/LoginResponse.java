package com.korotkov.messenger.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private int id;
    private String login;
    private String token;
}