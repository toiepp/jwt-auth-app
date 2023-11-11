package me.mikholsky.jwtauthstudy.controller;

import me.mikholsky.jwtauthstudy.controller.body.AuthenticationRequest;
import me.mikholsky.jwtauthstudy.controller.body.TokenResponse;
import me.mikholsky.jwtauthstudy.controller.body.RegistrationRequest;
import me.mikholsky.jwtauthstudy.service.AuthService;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private AuthService authService;

    @Autowired
    public AuthenticationController setAuthService(AuthService authService) {
        this.authService = authService;
        return this;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegistrationRequest request) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
