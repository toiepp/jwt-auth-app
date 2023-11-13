package me.mikholsky.jwtauthstudy.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVerificationDto {
    private String token;
    private String status;
    private String message;
}
