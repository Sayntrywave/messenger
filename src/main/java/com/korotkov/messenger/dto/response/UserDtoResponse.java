package com.korotkov.messenger.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class UserDtoResponse {
    String login;
    String surname;
    String name;
}
