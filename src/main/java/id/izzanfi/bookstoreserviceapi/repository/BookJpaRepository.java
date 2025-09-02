package id.izzanfi.bookstoreserviceapi.repository;

import id.izzanfi.bookstoreserviceapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

  @Query(value = "SELECT * FROM books WHERE author LIKE CONCAT('%',CONCAT(?1,'%'))", nativeQuery = true)
  Optional<List<Book>> findBooksByAuthor(String authorName);

  @Query(value = "SELECT * FROM books WHERE genre LIKE CONCAT('%',CONCAT(?1,'%'))", nativeQuery = true)
  Optional<List<Book>> findBooksByGenre(String genre);

  @Query(value = "SELECT * FROM books WHERE title LIKE CONCAT('%',CONCAT(?1,'%'))", nativeQuery = true)
  Optional<List<Book>> findBooksByBookTitle(String bookTitle);

  @Query(value = "SELECT * FROM books WHERE book_id IN (SELECT book_id FROM user_books WHERE user_id = ?1)", nativeQuery = true)
  Optional<List<Book>> findBooksByUser(Long userId);

  Boolean existsByBookTitle(String bookTitle);
}
