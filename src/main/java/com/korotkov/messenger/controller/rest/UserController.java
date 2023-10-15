package com.korotkov.messenger.controller.rest;

import com.korotkov.messenger.dto.request.AcceptFriendRequest;
import com.korotkov.messenger.dto.request.FriendDtoRequest;
import com.korotkov.messenger.dto.request.UserEditRequest;
import com.korotkov.messenger.dto.response.MessageResponse;
import com.korotkov.messenger.dto.response.UserDtoResponse;
import com.korotkov.messenger.model.Message;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.service.JWTService;
import com.korotkov.messenger.service.MessageService;
import com.korotkov.messenger.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    MessageService messageService;
    ModelMapper modelMapper;

    JWTService jwtService;

    @Autowired
    public UserController(UserService userService, MessageService messageService, ModelMapper modelMapper, JWTService jwtService) {
        this.userService = userService;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }

    @PutMapping("/user/edit")
    public ResponseEntity<Map<String, String>> editUser(@RequestBody @Valid UserEditRequest userEditRequest,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }
        User map = modelMapper.map(userEditRequest, User.class);

        boolean update = userService.update(map);
        if (update) {
            String token = jwtService.generateToken(userEditRequest.getLogin());
            return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<HttpStatus> deleteUser(){

        userService.delete();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@RequestParam(value = "nick",required = false) String nickname){
        List<Message> messagesFrom = messageService.getMessagesWith(userService.getCurrentUser().getId(), userService.findByLogin(nickname).getId());

        List<MessageResponse> collect = messagesFrom.stream()
                .map(message ->
                        MessageResponse.builder()
                                .message(message.getMessage())
                                .nicknameFrom(message.getUserFrom().getLogin())
                                .nicknameTo(message.getUserTo().getLogin())
                                .build())
                .collect(Collectors.toList());
        return new ResponseEntity<>(collect,HttpStatus.OK);
    }

    @PostMapping("/user/add-friend-request")
    public ResponseEntity<HttpStatus> addFriendRequest(@RequestBody @Valid FriendDtoRequest friendDtoRequest){
        userService.addFriendRequest(friendDtoRequest.getFriendLogin());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/user/accept-friend-request")
    public ResponseEntity<HttpStatus> addFriendRequest(@RequestBody @Valid AcceptFriendRequest acceptFriendRequest){
        userService.acceptFriendRequest(acceptFriendRequest.getFriendLogin(), acceptFriendRequest.isAccepted());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/friends")
    public ResponseEntity<List<UserDtoResponse>> getFriends(@RequestParam(value = "nick",required = false) String nickname){
        List<User> friends;

        if(nickname != null){
            friends = userService.getFriends(nickname);
        }else {
            friends = userService.getMyFriends();
        }


        return new ResponseEntity<>(friends.stream().
                map(user -> modelMapper.
                        map(user, UserDtoResponse.class))
                .toList(),HttpStatus.OK);
    }
}
