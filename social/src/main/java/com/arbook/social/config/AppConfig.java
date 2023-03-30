package com.arbook.social.config;

import com.arbook.social.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private static final int CORS_FILTER_ORDER = -102;

    private static final Long MAX_AGE = 3600L;

    private final UserRepository userRepository;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOriginPattern("https://**");
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                "X-XSRF-TOKEN",
                HttpHeaders.ACCEPT
        ));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name(),
                HttpMethod.PATCH.name()
        ));
        config.setExposedHeaders(Arrays.asList("CSRF-TOKEN-VALUE"));

        config.setMaxAge(MAX_AGE);

        source.registerCorsConfiguration("/**", config);



        return source;
    }




    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // authenticationProvider is the data access object which is responsible ot fetch
        // the user details and encode passwords
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService()); // tell authenticationProvider which UserDetailsService to use
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // tell provider which password encoder to use
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // returns password encoder we use
        return new BCryptPasswordEncoder();
    }
}
