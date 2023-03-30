package com.arbook.social.controller.auth;


import com.arbook.social.service.AuthenticationService;
import com.arbook.social.util.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(
                RegisterResponse
                .builder()
                .status(authenticationService.register(request))
                .build()
        );

    }



    @PostMapping("/confirm-registration")
    public ResponseEntity<AuthenticationResponse> confirmRegistration(
            @RequestBody ConfirmRegistrationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.confirmRegistration(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
