package com.had.backend.patient.entity;

import com.had.backend.patient.utils.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String aadhar;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String godFatherName;
    private String godFatherNumber;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dateOfBirth;
    @Setter(AccessLevel.NONE) private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return aadhar;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setAge() {
        this.age = Period.between(LocalDate.from(this.dateOfBirth), LocalDate.now()).getYears();
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
