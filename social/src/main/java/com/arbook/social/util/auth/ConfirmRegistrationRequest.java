package com.arbook.social.util.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmRegistrationRequest {
    private String email;
    private String activationCode;
}
