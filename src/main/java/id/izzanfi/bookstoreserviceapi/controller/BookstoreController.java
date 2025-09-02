package id.izzanfi.bookstoreserviceapi.controller;

import id.izzanfi.bookstoreserviceapi.dto.request.UpdateBookStockRequest;
import id.izzanfi.bookstoreserviceapi.dto.request.UpdateBookstoreRequest;
import id.izzanfi.bookstoreserviceapi.dto.response.BookStockResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.GenericMessageResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.UpdateBookstoreResponse;
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
@RequestMapping("/api/bookstores/")
public class BookstoreController {

    @Autowired
    private BookstoreJpaRepository bookstoreJpaRepository;

    @Autowired
    private BookStockJpaRepository bookStockJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @GetMapping("/list")
    public ResponseEntity<?> getListOfBookstores(Authentication authentication) {
        if (authentication.getPrincipal() instanceof String) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "Login first to see list of bookstores"));
        } else {
            return ResponseEntity.ok(bookstoreJpaRepository.findAll());
        }
    }

    @GetMapping("/detail/{bookstoreId}")
    public ResponseEntity<?> getBookstoreDetails(@PathVariable("bookstoreId") Long bookstoreId, Authentication authentication) {
        if (authentication.getPrincipal() instanceof String) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "Login first to see list of bookstores"));
        } else {
            if (bookstoreJpaRepository.existsById(bookstoreId)) {
                Bookstore bookstore = bookstoreJpaRepository.findById(bookstoreId)
                        .orElseThrow(() -> new DataNotFoundException(
                                String.format("Bookstore with id: %d cannot be found", bookstoreId)));

                return ResponseEntity.ok(bookstore);
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new GenericMessageResponse(
                                false, String.format("Bookstore with id: %d cannot be found", bookstoreId)));
            }
        }
    }

    @GetMapping("/search/name/{bookstoreName}")
    public ResponseEntity<?> searchBookstoresByName(@PathVariable("bookstoreName") String bookstoreName) {
        if (bookstoreJpaRepository.existsByBookstoreName(bookstoreName)) {
            List<Bookstore> searchResult = bookstoreJpaRepository.findBookstoresByName(bookstoreName)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Bookstore with name: %s cannot be found.", bookstoreName)));

            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Bookstore with name: %s cannot be found.", bookstoreName)));
        }
    }

    @GetMapping("/search/address/{bookstoreAddress}")
    public ResponseEntity<?> searchBookstoresByAddress(@PathVariable("bookstoreAddress") String bookstoreAddress) {
        if (bookstoreJpaRepository.existsByBookstoreName(bookstoreAddress)) {
            List<Bookstore> searchResult = bookstoreJpaRepository.findBookstoresByAddress(bookstoreAddress)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Bookstore with address: %s cannot be found.", bookstoreAddress)));

            return ResponseEntity.ok(searchResult);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(
                            false, String.format("Bookstore with address: %s cannot be found.", bookstoreAddress)));
        }
    }

    @GetMapping("/search/stock/{bookstoreId}")
    public ResponseEntity<?> getBookStocksListInABookstore(@PathVariable("bookstoreId") Long bookstoreId, Authentication authentication) {
        if (authentication.getPrincipal() instanceof String) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "Login first to see stock of book in a bookstore"));
        } else {
            List<BookStock> searchResult = bookStockJpaRepository.findBookStocksByBookstore(bookstoreId)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Bookstore with id: %d have no books available", bookstoreId)));
            List<Book> books = searchResult.stream().map(BookStock::getBook).toList();
            List<Bookstore> bookstores = searchResult.stream().map(BookStock::getBookstore).toList();

            List<BookStockResponse> responseBody = getBookStockResponseList(searchResult, books, bookstores);
            return ResponseEntity.ok(responseBody);
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

    @PostMapping("/superuser/createBookstore")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public Bookstore addNewBookstore(@Valid @RequestBody Bookstore bookstore) {
        log.info("[/superuser/createBookstore] Bookstore Creation (Superuser-only) endpoint invoked!");
        UpdateBookstoreResponse response = new UpdateBookstoreResponse();
        Bookstore newData = bookstoreJpaRepository.save(bookstore);

        return bookstoreJpaRepository.save(bookstore);
    }

    @DeleteMapping("/superuser/bookstore/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<?> deleteBookstore(@PathVariable("id") Long bookstoreId) {
        Bookstore bookstore = bookstoreJpaRepository.findById(bookstoreId).orElseThrow(() -> new DataNotFoundException(
                String.format("Bookstore with id: %d cannot be found!", bookstoreId)));

        log.info(String.format("[/superuser/bookstore/%d] Bookstore Deletion (Superuser-only) endpoint invoked!", bookstoreId));
        bookstoreJpaRepository.delete(bookstore);
        return ResponseEntity.ok(new GenericMessageResponse(true,
                String.format("Bookstore with id: %d has been deleted!", bookstoreId)));
    }

    @PutMapping("/admin/bookstore/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERUSER','ROLE_STORE_ADMIN')")
    public ResponseEntity<?> updateBookstoreData(@PathVariable("id") Long bookstoreId, @Valid @RequestBody UpdateBookstoreRequest requestBody) {
        if (bookstoreJpaRepository.existsById(bookstoreId)) {
            UpdateBookstoreResponse response = new UpdateBookstoreResponse();
            Bookstore currentBookstore = bookstoreJpaRepository.findById(bookstoreId).orElseThrow(() -> new DataNotFoundException(
                    String.format("Bookstore with id: %d cannot be found!", bookstoreId)));

            if ((requestBody.getBookStockRequest() != null) && !(requestBody.getBookStockRequest().isEmpty())) {
                Set<BookStock> updatedBookStocks = new HashSet<>();
                List<Book> books = bookJpaRepository.findAllById(
                        requestBody.getBookStockRequest().stream()
                                .map(UpdateBookStockRequest::getBookId)
                                .collect(Collectors.toSet()));
                List<Integer> bookStocks = requestBody.getBookStockRequest().stream()
                        .map(UpdateBookStockRequest::getBookStock)
                        .toList();

                for (int i = 0; i < books.size(); i++) {
                    BookStock newBookStockData = bookStockJpaRepository.findBookStockByBookAndBookstore(
                            books.get(i).getBookId(), currentBookstore.getBookstoreId()).orElse(new BookStock());

                    newBookStockData.setBook(books.get(i));
                    newBookStockData.setBookstore(currentBookstore);
                    newBookStockData.setBookStock(bookStocks.get(i));

                    BookStock savedData = bookStockJpaRepository.save(newBookStockData);
                    updatedBookStocks.add(savedData);
                }

                currentBookstore.setBookStocks(updatedBookStocks);
            }

            if ((requestBody.getBookstoreName() != null) && !(requestBody.getBookstoreName().isEmpty())) {
                currentBookstore.setBookstoreName(requestBody.getBookstoreName());
            }

            if ((requestBody.getIsActive() != null) && (requestBody.getIsActive() != currentBookstore.isActive())) {
                currentBookstore.setActive(requestBody.getIsActive());
            }

            if ((requestBody.getBookstoreAddress() != null) && !(requestBody.getBookstoreAddress().isEmpty())) {
                currentBookstore.setBookstoreAddress(requestBody.getBookstoreAddress());
            }

            if ((requestBody.getBookstorePhoneNumber() != null) && !(requestBody.getBookstorePhoneNumber().isEmpty())) {
                currentBookstore.setBookstorePhoneNo(requestBody.getBookstorePhoneNumber());
            }

            log.info(String.format("[/admin/bookstore/%d] Bookstore Data Update (Admin and Superuser-only) endpoint invoked!", bookstoreId));
            Bookstore updatedBookstore = bookstoreJpaRepository.save(currentBookstore);

            List<BookStockResponse> bookstoreStockData = getBookstoreStockData(updatedBookstore);
            response.setBookstoreId(currentBookstore.getBookstoreId());
            response.setBookstoreName(currentBookstore.getBookstoreName());
            response.setBookstoreAddress(currentBookstore.getBookstoreAddress());
            response.setBookstorePhoneNo(currentBookstore.getBookstorePhoneNo());
            response.setIsActive(currentBookstore.isActive());
            response.setBookstoreStockData(bookstoreStockData);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericMessageResponse(false,
                            String.format("Bookstore with id: %d is not found", bookstoreId)));
        }
    }

    private static List<BookStockResponse> getBookstoreStockData(Bookstore updatedBookstore) {
        List<BookStockResponse> bookstoreStockData = new ArrayList<>();
        for (BookStock bookStockData : updatedBookstore.getBookStocks()) {
            BookStockResponse respData = new BookStockResponse();
            respData.setBookstoreId(updatedBookstore.getBookstoreId());
            respData.setBookId(bookStockData.getBook().getBookId());

            respData.setBookstoreName(updatedBookstore.getBookstoreName());
            respData.setBookstoreAddress(updatedBookstore.getBookstoreAddress());
            respData.setBookstorePhoneNumber(updatedBookstore.getBookstorePhoneNo());

            respData.setIsbn(bookStockData.getBook().getBookIsbn());
            respData.setBookTitle(bookStockData.getBook().getBookTitle());
            respData.setBookAuthor(bookStockData.getBook().getBookAuthor());
            respData.setBookStock(bookStockData.getBookStock());

            bookstoreStockData.add(respData);
        }
        return bookstoreStockData;
    }
}
