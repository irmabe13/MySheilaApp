package com.irma.mysheila.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_user")
  private Integer idUser;

  @Column(name = "firstname", nullable = false, length = 50)
  private String firstname;

  @Column(name = "lastname", nullable = false, length = 100)
  private String lastname;

  @Column(name = "email", nullable = false, length = 150, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @CreationTimestamp
  @Column(name = "create_at", nullable = false)
  private LocalDate createAt;

  @UpdateTimestamp
  @Column(name = "modified_at", nullable = false)
  private LocalDate modifiedAt;

  @Builder.Default
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_role", nullable = false)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of((GrantedAuthority) () -> role.getName());
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
    return enabled;
  }
}
