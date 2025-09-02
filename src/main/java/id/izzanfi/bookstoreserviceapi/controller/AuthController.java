package id.izzanfi.bookstoreserviceapi.controller;

import id.izzanfi.bookstoreserviceapi.auth.CustomUserDetails;
import id.izzanfi.bookstoreserviceapi.config.JwtUtils;
import id.izzanfi.bookstoreserviceapi.dto.request.LoginRequest;
import id.izzanfi.bookstoreserviceapi.dto.request.SignupRequest;
import id.izzanfi.bookstoreserviceapi.dto.response.GenericMessageResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.JwtAuthResponse;
import id.izzanfi.bookstoreserviceapi.model.Role;
import id.izzanfi.bookstoreserviceapi.model.User;
import id.izzanfi.bookstoreserviceapi.model.UserRole;
import id.izzanfi.bookstoreserviceapi.repository.RoleJpaRepository;
import id.izzanfi.bookstoreserviceapi.repository.UserJpaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserJpaRepository userRepository;

    @Autowired
    RoleJpaRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUserRequest(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwtToken = jwtUtils.generateJwtToken(auth);

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        List<String> userRoles = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();

        return ResponseEntity.ok(new JwtAuthResponse(
                jwtToken, userDetails.getUserId(), userDetails.getUsername(), userDetails.getUserEmail(), userRoles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    new GenericMessageResponse(false,
                            "Error: Username " + signupRequest.getUsername() + " is already exists!"));
        }

        if (userRepository.existsByUserEmail(signupRequest.getUserEmail())) {
            return ResponseEntity.badRequest().body(
                    new GenericMessageResponse(false,
                            "Error: Email " + signupRequest.getUserEmail() + " is already exists!"));
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getUserEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));
        Set<String> userRoles = signupRequest.getUserRoles();
        Set<Role> roles = new HashSet<>();

        if (userRoles == null) {
            Role userRole = roleRepository.findByRoleName(UserRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not exist"));
            roles.add(userRole);
        } else {
            userRoles.forEach(userRole -> {
                switch (UserRole.valueOf(userRole)) {
                    case ROLE_STORE_ADMIN:
                        Role storeAdminRole = roleRepository.findByRoleName(UserRole.ROLE_STORE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not exist"));
                        roles.add(storeAdminRole);
                        break;

                    case ROLE_SUPERUSER:
                        Role superUserRole = roleRepository.findByRoleName(UserRole.ROLE_SUPERUSER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not exist"));
                        roles.add(superUserRole);
                        break;

                    default:
                        Role ordinaryUserRole = roleRepository.findByRoleName(UserRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not exist"));
                        roles.add(ordinaryUserRole);
                }
            });
        }

        // saving the user to database using JPA
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new GenericMessageResponse(true, "User registered successfully!"));
    }
}
