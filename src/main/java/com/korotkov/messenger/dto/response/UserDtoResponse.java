package com.korotkov.messenger.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDtoResponse {
    String login;
    String surname;
    String name;
}
