package me.mikholsky.jwtauthstudy.controller;

import me.mikholsky.jwtauthstudy.controller.body.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {
    @GetMapping
    public ResponseEntity<String> sayHello() {
        SecurityContext context = SecurityContextHolder.getContext();

        Authentication authentication = context.getAuthentication();

        System.out.println(authentication);

        Object principal = authentication.getPrincipal();

        System.out.println(principal);

        return ResponseEntity.ok("I'm secured!");
    }
}
