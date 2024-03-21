package dev.arun.ecomuserservice.controller;

import dev.arun.ecomuserservice.dto.LoginRequestDto;
import dev.arun.ecomuserservice.dto.SignUpRequestDto;
import dev.arun.ecomuserservice.dto.UserDto;
import dev.arun.ecomuserservice.dto.ValidateTokenRequestDto;
import dev.arun.ecomuserservice.models.Session;
import dev.arun.ecomuserservice.models.SessionStatus;
import dev.arun.ecomuserservice.models.User;
import dev.arun.ecomuserservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class Authocontroller {
    private AuthService authService;

    public Authocontroller(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<Void> logout(@PathVariable("id") Long userId, @RequestHeader("token") String token) {
        return authService.logout(token, userId);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }

    // The below APIs are only for learning purposes, should not be present in actual systems
    @GetMapping("/session")
    public ResponseEntity<List<Session>> getAllSession(){
        return authService.getAllSession();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return authService.getAllUsers();
    }
}
