package com.rms.dto.response;
import com.rms.entity.User;
import lombok.*;
import java.time.LocalDateTime;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id; private String fullName; private String email;
    private String phone; private String role; private Boolean isActive;
    private LocalDateTime createdAt;
    public static UserResponse from(User u){
        return UserResponse.builder().id(u.getId()).fullName(u.getFullName())
            .email(u.getEmail()).phone(u.getPhone()).role(u.getRole().name())
            .isActive(u.getIsActive()).createdAt(u.getCreatedAt()).build(); }
}
