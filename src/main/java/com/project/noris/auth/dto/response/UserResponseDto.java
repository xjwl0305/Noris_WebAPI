package com.project.noris.auth.dto.response;

import com.project.noris.PCutilization.dto.OrganizationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }

}
