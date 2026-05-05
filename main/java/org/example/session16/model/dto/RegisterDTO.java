package org.example.session16.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String address;
}

