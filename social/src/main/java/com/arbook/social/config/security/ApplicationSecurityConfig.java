package com.arbook.social.config.security;


import com.arbook.social.config.security.filter.CsrfLoggerFilter;
import com.arbook.social.config.security.filter.JWTAuthFilter;
import com.arbook.social.security.RestAuthenticationEntryPoint;
import com.arbook.social.security.oauth2.CustomOAuth2UserService;
import com.arbook.social.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.arbook.social.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.arbook.social.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfLogoutHandler;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final JWTAuthFilter jwtAuthFilter;
    

    private static final String[] SWAGGER_URLS = {
            // -- swagger ui
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/configuration/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
    };

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }


    private final String[] cookieNamesToClear = {
            "Authorization",
            "_xsrf",
            "SESSION",
            "XSRF-TOKEN"
    };
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // telling spring to store csrf token in a cookie
                .and()
                .cors() // allows spring to handle preflight requests
                .and()
                .addFilterAfter(new CsrfLoggerFilter(), CsrfFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
        // http only to false sets my frontend to get the value of the cookie
                .authorizeHttpRequests()
                .requestMatchers(SWAGGER_URLS)
                .permitAll()
                .requestMatchers("/csrf", "/auth/**", "/logout")
                .permitAll()    // permit all urls above
                .requestMatchers("/", "/error", "/login/**", "/oauth/**", "/oauth2/**")
                .permitAll()
                .anyRequest()   // we want user to be authenticated to use any other urls
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // spring will create new session for each request
                .and()
                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // before is because we want this filter to be executed before usernamePasswordAuthenticationFilter to update security context holder at first
                // and then we can use UsernamePasswordAuthenticationFilter;

        return http.build();
    }
}
