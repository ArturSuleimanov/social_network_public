package com.arbook.social.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        // because either username or email is the subject of our token
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        // extracting one single claim
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        // creating secret key by using our SECRET_KEY
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);    // decoding algorithm
    }


    public String generateToken(
            UserDetails userDetails
    ) {
        // generating token without extra claims
        return generateToken(new HashMap<>(), userDetails);
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // generates jwt token
        return "Bearer " + Jwts
                .builder()
                .setClaims(extraClaims) // adding additional claims to our token
                .setSubject(userDetails.getUsername())  // setting unique user identifier as token
                .setIssuedAt(new Date(System.currentTimeMillis()))  // setting issue date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 60))  // setting expiration date (2 month)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)   // telling spring which sign in key and algorithm to use
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        // we need user details because we want to validate if this token belongs to this user details
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}