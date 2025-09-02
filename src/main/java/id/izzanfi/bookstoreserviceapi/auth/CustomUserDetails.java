package id.izzanfi.bookstoreserviceapi.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import id.izzanfi.bookstoreserviceapi.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"password"})
public class CustomUserDetails implements UserDetails {

  private Long userId;
  private String userName;
  private String userEmail;

  @JsonIgnore
  private String userPassword;

  private Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(Long userId, String userName, String userEmail,
      String userPassword, Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.userName = userName;
    this.userEmail = userEmail;
    this.userPassword = userPassword;
    this.authorities = authorities;
  }

  public static CustomUserDetails build(User user) {
    List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(role ->
        new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
    return new CustomUserDetails(
        user.getUserId(),
        user.getUserName(),
        user.getUserEmail(),
        user.getUserPassword(),
        grantedAuthorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return userPassword;
  }

  @Override
  public String getUsername() {
    return userName;
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    CustomUserDetails user = (CustomUserDetails) obj;
    return Objects.equals(userId, user.userId);
  }
}
