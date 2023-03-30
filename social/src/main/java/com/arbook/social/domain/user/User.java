package com.arbook.social.domain.user;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;


    @Size.List({
            @Size(min = 2),
            @Size(max = 20)
    })
    @NotNull
    private String firstname;

    private AuthProvider provider;

    private String providerId;

    @Size.List({
            @Size(min = 2),
            @Size(max = 20)
    })
    @NotNull
    private String lastname;

    @NotNull
    @Column(unique = true)
    @Email
    private String email;



    @Size.List({
            @Size(min = 8),
            @Size(max = 1000)
    })
    @NotNull
    private String password;
    private String activationCode;

    private Date activationCodeExpiration;

    private boolean isActive;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isActivationCodeNonExpired() {
        return this.getActivationCodeExpiration().after(new Date());
    }
}
