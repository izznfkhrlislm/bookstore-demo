package id.izzanfi.bookstoreserviceapi.config;

import id.izzanfi.bookstoreserviceapi.auth.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditImpl implements AuditorAware<String> {

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
      return Optional.of("SYSTEM");
    } else {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      CustomUserDetails authPrincipal = (CustomUserDetails) authentication.getPrincipal();

      return Optional.ofNullable(authPrincipal.getUsername());
    }
  }
}
