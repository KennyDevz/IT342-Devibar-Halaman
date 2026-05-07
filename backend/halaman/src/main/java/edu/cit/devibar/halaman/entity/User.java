package edu.cit.devibar.halaman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 20)
    private String status = "ACTIVE";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) role = Role.USER;
    }

    // --- Enum ---
    public enum Role {
        USER, ADMIN
    }

    // --- UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword()              { return passwordHash; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }



    // --- Getters ---
    public UUID getUserId()            { return userId; }
    public String getEmail()           { return email; }
    public String getPasswordHash()    { return passwordHash; }
    public String getGoogleId()        { return googleId; }
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public Role getRole()              { return role; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public String getStatus() { return status; }

    // --- Setters ---
    public void setUserId(UUID userId)             { this.userId = userId; }
    public void setEmail(String email)             { this.email = email; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }
    public void setGoogleId(String googleId)       { this.googleId = googleId; }
    public void setFirstName(String firstName)     { this.firstName = firstName; }
    public void setLastName(String lastName)       { this.lastName = lastName; }
    public void setRole(Role role)                 { this.role = role; }
    public void setStatus(String status) { this.status = status; }

}