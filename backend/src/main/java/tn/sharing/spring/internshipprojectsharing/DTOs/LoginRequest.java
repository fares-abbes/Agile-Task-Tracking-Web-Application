package tn.sharing.spring.internshipprojectsharing.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}