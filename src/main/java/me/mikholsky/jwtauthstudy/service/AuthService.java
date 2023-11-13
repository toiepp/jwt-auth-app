package me.mikholsky.jwtauthstudy.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import me.mikholsky.jwtauthstudy.controller.body.AuthenticationRequest;
import me.mikholsky.jwtauthstudy.controller.body.TokenResponse;
import me.mikholsky.jwtauthstudy.controller.body.RegistrationRequest;
import me.mikholsky.jwtauthstudy.entity.Role;
import me.mikholsky.jwtauthstudy.entity.User;
import me.mikholsky.jwtauthstudy.repository.TokenRepository;
import me.mikholsky.jwtauthstudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private UserRepository userRepository;

    private JwtService jwtService;

    private TokenRepository tokenRepository;

    @Autowired
    public AuthService setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    @Autowired
    public AuthService setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
        return this;
    }

    @Autowired
    public AuthService setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        return this;
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No such user"));

        var jwtToken = jwtService.generateToken(
                Map.of("jti", UUID.randomUUID().toString()),
                user
        );

        var token = TokenResponse.builder()
                .id(jwtService.extractId(jwtToken))
                .token(jwtToken)
                .build();

        tokenRepository.save(token);

        return TokenResponse.builder()
                .token(jwtToken)
                .build();
    }

    public boolean verify(String token) {
        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return false;
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No such user"));

        return jwtService.isTokenValid(token, user);
    }
}
