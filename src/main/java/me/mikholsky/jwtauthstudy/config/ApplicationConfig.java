package me.mikholsky.jwtauthstudy.config;

import me.mikholsky.jwtauthstudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

@Configuration
@EnableRedisRepositories
public class ApplicationConfig {
    private UserRepository userRepository;

    @Autowired
    public ApplicationConfig setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user with username " + username));
    }
}
