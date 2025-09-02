package id.izzanfi.bookstoreserviceapi.auth;

import id.izzanfi.bookstoreserviceapi.model.User;
import id.izzanfi.bookstoreserviceapi.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  UserJpaRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    User user = userRepository.findUserByName(userName)
        .orElseThrow(() ->
            new UsernameNotFoundException("User dengan username: " + userName + " tidak dapat ditemukan!"));
    return CustomUserDetails.build(user);
  }
}
