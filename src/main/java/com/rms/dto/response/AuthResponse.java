package com.rms.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String email;
    private String fullName;
    private String role;
    public static AuthResponse of(String at, String rt, String email, String name, String role){
        return AuthResponse.builder().accessToken(at).refreshToken(rt)
            .tokenType("Bearer").email(email).fullName(name).role(role).build(); }
}
