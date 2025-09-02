package id.izzanfi.bookstoreserviceapi.repository;

import id.izzanfi.bookstoreserviceapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  @Query(value = "SELECT * FROM users where name = ?1", nativeQuery = true)
  Optional<User> findUserByName(String userName);

  Boolean existsByUserName(String userName);
  Boolean existsByUserEmail(String userEmail);
  Boolean existsByUserId(Long userId);
}
