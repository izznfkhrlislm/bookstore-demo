package id.izzanfi.bookstoreserviceapi.repository;

import id.izzanfi.bookstoreserviceapi.model.Role;
import id.izzanfi.bookstoreserviceapi.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleName(UserRole roleName);
}
