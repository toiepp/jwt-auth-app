package me.mikholsky.jwtauthstudy.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.mikholsky.jwtauthstudy.controller.body.TokenResponse;
import me.mikholsky.jwtauthstudy.repository.TokenRepository;
import me.mikholsky.jwtauthstudy.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class OncePerRequestJwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;

    private TokenRepository tokenRepository;

    private KeyValueTemplate keyValueTemplate;

    @Autowired
    public OncePerRequestJwtAuthenticationFilter setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
        return this;
    }

    @Autowired
    public OncePerRequestJwtAuthenticationFilter setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    @Autowired
    public OncePerRequestJwtAuthenticationFilter setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        return this;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (!(authHeader == null || !authHeader.startsWith("Bearer "))) {
            final String jwt = authHeader.substring(7);

            String tokenId = jwtService.extractId(jwt);

            tokenRepository.findById(tokenId)
                    .ifPresent(tokenResponse -> {
                        if (tokenResponse.getToken().equals(jwt)) {
                            var userDetails =
                                    userDetailsService.loadUserByUsername(
                                            jwtService.extractUsername(tokenResponse.getToken())
                                    );

                            var authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    });
        }
        filterChain.doFilter(request, response);
    }
}
