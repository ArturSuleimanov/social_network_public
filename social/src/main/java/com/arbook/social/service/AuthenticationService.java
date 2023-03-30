package com.arbook.social.service;


import com.arbook.social.util.*;
import com.arbook.social.repo.UserRepository;
import com.arbook.social.domain.user.Role;
import com.arbook.social.domain.user.User;
import com.arbook.social.util.auth.AuthenticationRequest;
import com.arbook.social.util.auth.AuthenticationResponse;
import com.arbook.social.util.auth.ConfirmRegistrationRequest;
import com.arbook.social.util.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MyMailSender registerMailSender;
    private final StringGenerator stringGenerator;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static String subject = "Аctivation code.";
    private static String message = "Your activation code: %s.";

    public int register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // check if user exists
            return 300;
        }




        String activationCode = stringGenerator.generateString();
        // allows us to register user, save it to the db and send generated token as a request
        var user = User.builder()   // saving our user to db
                .activationCode(activationCode)
                .isActive(false)
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        user.setActivationCodeExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24));
        userRepository.save(user);

        registerMailSender.send(
                user.getEmail(),
                subject,
                String.format(message, activationCode)
        );
        return 200;

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );  // this manager will authenticate user for us


        // if we got here, it means username and password are correct
        // and if they are correct,  we need to generate a token and send it back
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isActive()) {
            return AuthenticationResponse
                    .builder()
                    .status(300)
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse   // returning auth response
                .builder()
                .status(200)
                .token(jwtToken)    // adding our token to our response
                .build();
    }

    public AuthenticationResponse confirmRegistration(ConfirmRegistrationRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        String jwtToken = null;
        if (user.getActivationCode().equals(request.getActivationCode()) && user.isActivationCodeNonExpired()) {
            user.setActive(true);
            user.setActivationCode(null);
            user.setActivationCodeExpiration(null);
            userRepository.save(user);
            jwtToken = jwtService.generateToken(user);
        }
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();

    }
}
