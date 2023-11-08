package me.mikholsky.jwtauthstudy.controller.body;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticationRequest {
    private String email;

    private String password;
}
