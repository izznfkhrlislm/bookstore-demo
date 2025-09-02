package id.izzanfi.bookstoreserviceapi.controller;

import id.izzanfi.bookstoreserviceapi.auth.CustomUserDetails;
import id.izzanfi.bookstoreserviceapi.dto.request.UpdateBookRequest;
import id.izzanfi.bookstoreserviceapi.dto.request.UpdateBookStockRequest;
import id.izzanfi.bookstoreserviceapi.dto.response.BookStockResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.GenericMessageResponse;
import id.izzanfi.bookstoreserviceapi.exception.DataNotFoundException;
import id.izzanfi.bookstoreserviceapi.model.Book;
import id.izzanfi.bookstoreserviceapi.model.BookStock;
import id.izzanfi.bookstoreserviceapi.model.Bookstore;
import id.izzanfi.bookstoreserviceapi.repository.BookJpaRepository;
import id.izzanfi.bookstoreserviceapi.repository.BookStockJpaRepository;
import id.izzanfi.bookstoreserviceapi.repository.BookstoreJpaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/books/")
public class BookController {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private BookStockJpaRepository bookStockJpaRepository;

    @Autowired
    private BookstoreJpaRepository bookstoreJpaRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getBooksListByLoggedInUser(Authentication authentication) {
        CustomUserDetails authPrincipal = (CustomUserDetails) authentication.getPrincipal();
        if (authPrincipal != null) {
            return ResponseEntity.ok(bookJpaRepository.findBooksByUser(authPrincipal.getUserId()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "User is not logged in!"));
        }
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<?> searchBooksByTitle(@PathVariable("title") String bookTitle) {
        if (bookJpaRepository.existsByBookTitle(bookTitle)) {
            List<Book> searchResult = bookJpaRepository.findBooksByBookTitle(bookTitle)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Book with title: %s cannot be found", bookTitle)));

            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Book with title: %s cannot be found.", bookTitle)));
        }
    }

    @GetMapping("/search/author/{author}")
    public ResponseEntity<?> searchBookByAuthor(@PathVariable("author") String bookAuthor) {
        if (bookJpaRepository.existsByBookTitle(bookAuthor)) {
            List<Book> searchResult = bookJpaRepository.findBooksByAuthor(bookAuthor)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Book with author: %s cannot be found", bookAuthor)));

            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Book with author: %s cannot be found.", bookAuthor)));
        }
    }

    @GetMapping("/search/genre/{genre}")
    public ResponseEntity<?> searchBooksByGenre(@PathVariable("genre") String bookGenre) {
        if (bookJpaRepository.existsByBookTitle(bookGenre)) {
            List<Book> searchResult = bookJpaRepository.findBooksByGenre(bookGenre)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Book with genre: %s cannot be found", bookGenre)));

            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Book with genre: %s cannot be found.", bookGenre)));
        }
    }

    @GetMapping("/search/stock/{bookId}")
    public ResponseEntity<?> getBookStockList(@PathVariable("bookId") Long bookId) {
        if (bookJpaRepository.existsById(bookId)) {
            List<BookStock> searchResult = bookStockJpaRepository.findBookStocksByBook(bookId)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Book with id: %d have no available stock", bookId)));
            List<Book> books = searchResult.stream().map(BookStock::getBook).toList();
            List<Bookstore> bookstores = searchResult.stream().map(BookStock::getBookstore).toList();

            List<BookStockResponse> responseBody = getBookStockResponseList(searchResult, books, bookstores);
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Book with id: %d cannot be found.", bookId)));
        }
    }

    private static List<BookStockResponse> getBookStockResponseList(List<BookStock> searchResult, List<Book> books, List<Bookstore> bookstores) {
        List<BookStockResponse> responseBody = new ArrayList<>();
        for (int i = 0; i < searchResult.size(); i++) {
            BookStock currentBookStock = searchResult.get(i);
            Book currentBook = books.get(i);
            Bookstore currentBookstore = bookstores.get(i);
            BookStockResponse responseData = new BookStockResponse(
                    currentBook.getBookId(),
                    currentBookstore.getBookstoreId(),
                    currentBookStock.getBookStock(),
                    currentBook.getBookTitle(),
                    currentBook.getBookAuthor(),
                    currentBook.getBookIsbn(),
                    currentBookstore.getBookstoreName(),
                    currentBookstore.getBookstoreAddress(),
                    currentBookstore.getBookstorePhoneNo()
            );

            responseBody.add(responseData);
        }
        return responseBody;
    }

    /**
     * Admin and Superuser endpoints
     */

    @GetMapping("/superuser/getAllBooks")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public List<Book> getAllBooks() {
        return bookJpaRepository.findAll();
    }

    @PostMapping("/admin/createBook")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER','ROLE_STORE_ADMIN')")
    public Book addNewBook(@Valid @RequestBody Book book) {
        log.info("[/admin/createBook] Book Creation (Admin and Superuser-only) endpoint invoked!");
        return bookJpaRepository.save(book);
    }

    @DeleteMapping("/admin/book/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER','ROLE_STORE_ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookId) {
        Book book = bookJpaRepository.findById(bookId).orElseThrow(() -> new DataNotFoundException(
                String.format("Book with id: %d cannot be found!", bookId)));

        log.info(String.format("[/superuser/book/%d] Book Deletion (Admin and Superuser-only) endpoint invoked!", bookId));
        bookJpaRepository.delete(book);
        return ResponseEntity.ok(new GenericMessageResponse(true,
                String.format("Book with id: %d has been deleted!", bookId)));
    }

    @PutMapping("/admin/book/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER','ROLE_STORE_ADMIN')")
    public ResponseEntity<?> updateBookData(@PathVariable("id") Long bookId, @Valid @RequestBody UpdateBookRequest requestBody) {
        if (bookJpaRepository.existsById(bookId)) {
            Book currentBook = bookJpaRepository.findById(bookId).orElseThrow(() -> new DataNotFoundException(
                    String.format("Book with id: %d cannot be found!", bookId)));

            if ((requestBody.getBookStockRequest() != null) && !(requestBody.getBookStockRequest().isEmpty())) {
                Set<BookStock> updatedBookStocks = new HashSet<>();
                List<Bookstore> bookstores = bookstoreJpaRepository.findAllById(
                        requestBody.getBookStockRequest().stream()
                                .map(UpdateBookStockRequest::getBookstoreId)
                                .collect(Collectors.toSet()));
                List<Integer> bookStocks = requestBody.getBookStockRequest().stream()
                        .map(UpdateBookStockRequest::getBookStock)
                        .toList();

                for (int i = 0; i < bookstores.size(); i++) {
                    BookStock newBookStockData = bookStockJpaRepository.findBookStockByBookAndBookstore(
                            currentBook.getBookId(), bookstores.get(i).getBookstoreId()).orElse(new BookStock());;
                    newBookStockData.setBook(currentBook);
                    newBookStockData.setBookstore(bookstores.get(i));
                    newBookStockData.setBookStock(bookStocks.get(i));

                    BookStock savedData = bookStockJpaRepository.save(newBookStockData);
                    updatedBookStocks.add(savedData);
                }

                currentBook.setBookStocks(updatedBookStocks);
            }

            if ((requestBody.getBookAuthor() != null) && !(requestBody.getBookAuthor().isEmpty())) {
                currentBook.setBookAuthor(requestBody.getBookAuthor());
            }

            if ((requestBody.getBookTitle() != null) && !(requestBody.getBookTitle().isEmpty())) {
                currentBook.setBookTitle(requestBody.getBookTitle());
            }

            if ((requestBody.getIsbn() != null) && !(requestBody.getIsbn().isEmpty())) {
                currentBook.setBookIsbn(requestBody.getIsbn());
            }

            if ((requestBody.getBookGenre() != null) && !(requestBody.getBookGenre().isEmpty())) {
                currentBook.setBookGenre(requestBody.getBookGenre());
            }

            log.info(String.format("[/admin/book/%d] Book Data Update (Admin and Superuser-only) endpoint invoked!", bookId));
            Book updatedBook = bookJpaRepository.save(currentBook);
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(false,
                            String.format("Book with id: %d is not found", bookId)));
        }
    }
}
