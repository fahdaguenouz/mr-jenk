package com.buy01.user_service.models;

import com.buy01.user_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails { // <-- Add UserDetails

   @Id
    @Builder.Default // Tells Lombok to use this default value when building
    private String id = java.util.UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private String avatarMediaId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // --- SPRING SECURITY USERDETAILS METHODS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        // Since they can log in with email OR username, we return the username here 
        // as the primary identifier for the active token.
        return username; 
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}