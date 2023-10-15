package com.korotkov.messenger.controller.rest;

import com.korotkov.messenger.dto.request.AuthenticationRequest;
import com.korotkov.messenger.dto.request.RegistrationRequest;
import com.korotkov.messenger.dto.response.LoginResponse;
import com.korotkov.messenger.model.EmailUser;
import com.korotkov.messenger.model.User;
import com.korotkov.messenger.service.JWTService;
import com.korotkov.messenger.service.MailSenderService;
import com.korotkov.messenger.service.RegistrationService;
import com.korotkov.messenger.service.UserService;
import com.korotkov.messenger.util.UserNotCreatedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    UserService userService;
    AuthenticationManager authenticationManager;
    ModelMapper modelMapper;

    MailSenderService mailSenderService;

    RegistrationService registrationService;

    JWTService jwtService;


    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, ModelMapper modelMapper, MailSenderService mailSenderService, RegistrationService registrationService, JWTService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.mailSenderService = mailSenderService;
        this.registrationService = registrationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid RegistrationRequest user,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserNotCreatedException(bindingResult.getFieldError().getField() + " " + bindingResult.getFieldError().getDefaultMessage());
        }

        String token = registrationService.register(modelMapper.map(user, EmailUser.class));

        mailSenderService.send(user.getEmail(), "Регистрация", "http://localhost:8080/activate?t=" + token);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/activate")
    public ResponseEntity<HttpStatus> activate(@RequestParam(value = "t") String token,
                                               @RequestParam(value = "is-in-ban", required = false) Boolean isInBan,
                                               @Email @RequestParam(value = "email", required = false) String email) {
        registrationService.activate(token, isInBan, email);
        return new ResponseEntity<>(HttpStatus.OK);
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

        String token = jwtService.generateToken(authenticationRequest.getLogin());


        LoginResponse map = modelMapper.map(currentUser, LoginResponse.class);
        map.setToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<HttpStatus> logout(@RequestHeader(name = "Authorization") String token) {
        jwtService.invalidateToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
