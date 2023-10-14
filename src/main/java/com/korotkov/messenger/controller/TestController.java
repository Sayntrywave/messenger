package com.korotkov.messenger.controller;

import com.korotkov.messenger.dto.request.AuthenticationRequest;
import com.korotkov.messenger.dto.request.RegistrationRequest;
import com.korotkov.messenger.dto.response.LoginResponse;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.security.JWTUtil;
import com.korotkov.messenger.service.RegistrationService;
import com.korotkov.messenger.service.UserService;
import com.korotkov.messenger.util.UserNotCreatedException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {

    UserService userService;
    AuthenticationManager authenticationManager;
    ModelMapper modelMapper;

    RegistrationService registrationService;

    JWTUtil jwtUtil;


    @Autowired
    public TestController(UserService userService, AuthenticationManager authenticationManager, ModelMapper modelMapper, RegistrationService registrationService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/hello")
    public String getHello() {
        return "hello";
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registration(@RequestBody @Valid RegistrationRequest user,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserNotCreatedException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }

        registrationService.register(modelMapper.map(user, User.class));

        User currentUser = userService.findByLogin(user.getLogin());

        String token = jwtUtil.generateToken(currentUser.getLogin());
        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadCredentialsException(bindingResult.getFieldError().getDefaultMessage());
        }


        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getLogin(),
                authenticationRequest.getPassword()
        );

        authenticationManager.authenticate(authInputToken);


        User currentUser = userService.findByLogin(authenticationRequest.getLogin());

        String token = jwtUtil.generateToken(authenticationRequest.getLogin());


        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
