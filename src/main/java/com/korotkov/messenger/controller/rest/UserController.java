package com.korotkov.messenger.controller.rest;

import com.korotkov.messenger.dto.request.UserEditRequest;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.security.JWTUtil;
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

import java.util.Map;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    ModelMapper modelMapper;

    JWTUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
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
            String token = jwtUtil.generateToken(userEditRequest.getLogin());
            return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
