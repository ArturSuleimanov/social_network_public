package com.arbook.social.config.security.filter;

import com.arbook.social.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {


    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;    // this will help us to work with token

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");   // extracting header from request header by "Authorization" key
//        final String authHeader = Arrays.stream(request.getCookies())
//                .filter(c -> c.getName().equals("Authorization"))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElse(null);   // extracting header from request header by "Authorization" key




        final String userEmail;
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);    // giving this request to the next filter
            return;
        }

        jwt = authHeader.substring(7);  // getting rid of word "Bearer
        // now we need to check if such user exists by checking UserDetailsService
        // but first we need to call JwtService (and also create it) to extract username
        userEmail = jwtService.extractUsername(jwt); // extracting userEmail from jwt token
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // checking if user email exists and authenticated
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);    // fetching user by email from database
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // checking if token is valid
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,   // we don't have credentials
                        userDetails.getAuthorities()
                );  // we need this in order to update security context
                authenticationToken.setDetails(
                        // adding some more details
                        new WebAuthenticationDetailsSource().buildDetails(request)  // building our detailes out of our http request
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);  // updating security context holder
            }
            filterChain.doFilter(request, response);    // we need this to let next filter handle this request
        }
    }
}
