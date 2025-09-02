package id.izzanfi.bookstoreserviceapi.repository;

import id.izzanfi.bookstoreserviceapi.model.Book;
import id.izzanfi.bookstoreserviceapi.model.BookStock;
import id.izzanfi.bookstoreserviceapi.model.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookStockJpaRepository extends JpaRepository<BookStock, Long> {

  @Query(value = "SELECT * FROM book_stocks WHERE book_id = ?1", nativeQuery = true)
  Optional<List<BookStock>> findBookStocksByBook(Long bookId);

  @Query(value = "SELECT * FROM book_stocks WHERE bookstore_id = ?1", nativeQuery = true)
  Optional<List<BookStock>> findBookStocksByBookstore(Long bookstoreId);

  @Query(value = "SELECT * FROM book_stocks WHERE book_id = ?1 AND bookstore_id = ?2", nativeQuery = true)
  Optional<BookStock> findBookStockByBookAndBookstore(Long bookId, Long bookstoreId);

  @Query(value = "SELECT * FROM book_stocks WHERE bookstore_id = ?1 AND stock > 0", nativeQuery = true)
  Optional<List<BookStock>> findNonZeroBookStockByBookstore(Long bookstoreId);

  @Query(value = "SELECT * FROM book_stocks WHERE book_id = ?1 AND stock > 0", nativeQuery = true)
  Optional<List<BookStock>> findNonZeroBookStockByBook(Long bookId);

  Boolean existsByBookAndBookstore(Book book, Bookstore bookstore);
}
