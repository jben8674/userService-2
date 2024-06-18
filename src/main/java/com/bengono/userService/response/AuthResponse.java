package com.bengono.userService.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthResponse {

    private String jwt;
    private String message;
    private Boolean status;
}
