package com.invenza.security.service;

import com.invenza.entities.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;

    public CustomUserDetails(Users user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("Assigning authority: ROLE_" + role); // Debug log
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

}
