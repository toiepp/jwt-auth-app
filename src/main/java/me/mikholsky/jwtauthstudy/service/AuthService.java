package me.mikholsky.jwtauthstudy.service;

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

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    private TokenRepository tokenRepository;

    @Autowired
    public AuthService setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    @Autowired
    public AuthService setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    @Autowired
    public AuthService setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
        return this;
    }

    @Autowired
    public AuthService setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        return this;
    }

    @Autowired
    public AuthService setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        return this;
    }


    /**
     * Signing up new user in system and returns him a JWT token
     *
     * @param request class containing email and password
     * @return <strong>token</strong> which user can send with request
     */
    public void register(RegistrationRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

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
}
