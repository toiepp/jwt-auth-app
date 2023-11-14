package me.mikholsky.jwtauthstudy.controller;

import me.mikholsky.jwtauthstudy.controller.body.AuthenticationRequest;
import me.mikholsky.jwtauthstudy.controller.dto.TokenDto;
import me.mikholsky.jwtauthstudy.controller.dto.TokenVerificationDto;
import me.mikholsky.jwtauthstudy.service.AuthService;
import me.mikholsky.jwtauthstudy.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/jwt", produces = MediaType.APPLICATION_JSON_VALUE)
public class JwtController {
    private AuthService authService;

    private JwtService jwtService;

    @Autowired
    public JwtController setAuthService(AuthService authService) {
        this.authService = authService;
        return this;
    }

    @Autowired
    public JwtController setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
        return this;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> generateToken(@RequestBody AuthenticationRequest request) {
        var token = authService.authenticate(request);

        var dto = TokenDto.builder()
                .token(token.getToken())
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/verify")
    public ResponseEntity<TokenVerificationDto> verify(@RequestBody TokenDto tokenDto) {
        var validity = authService.verify(tokenDto.getToken());

        var res = TokenVerificationDto.builder();

        String email = jwtService.extractUsername(tokenDto.getToken());

        if (validity) {
            return ResponseEntity.ok(res.token(tokenDto.getToken())
                    .status("OK")
                    .message("Token is valid. Welcome!")
                    .email(email)
                    .build());
        } else {
            var body = res.token(tokenDto.getToken())
                    .status("INVALID")
                    .message("Token is NOT valid. Reauthenticate, please!")
                    .email(email)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
}
