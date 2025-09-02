package id.izzanfi.bookstoreserviceapi.repository;

import id.izzanfi.bookstoreserviceapi.model.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookstoreJpaRepository extends JpaRepository<Bookstore, Long> {

  @Query(value = "SELECT * FROM bookstores WHERE address LIKE CONCAT('%',CONCAT(?1,'%'))", nativeQuery = true)
  Optional<List<Bookstore>> findBookstoresByAddress(String bookstoreAddress);

  @Query(value = "SELECT * FROM bookstores WHERE name LIKE CONCAT('%',CONCAT(?1,'%'))", nativeQuery = true)
  Optional<List<Bookstore>> findBookstoresByName(String bookstoreName);

  @Query(value = "SELECT * FROM bookstores WHERE bookstore_id IN (SELECT bookstore_id FROM book_stocks WHERE book_id = ?1)", nativeQuery = true)
  Optional<List<Bookstore>> findBookstoresByBookId(Long bookId);

  @Query(value = "SELECT * FROM bookstores WHERE is_active = true", nativeQuery = true)
  Optional<List<Bookstore>> findActiveBookstores();

  Boolean existsByBookstoreName(String bookstoreName);
}
