package com.projectSpring.ecom.dto;

import com.projectSpring.ecom.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
    private Boolean active;
    private Long createdAt;
}
