package id.izzanfi.bookstoreserviceapi.controller;

import id.izzanfi.bookstoreserviceapi.auth.CustomUserDetails;
import id.izzanfi.bookstoreserviceapi.dto.request.UpdateUserRequest;
import id.izzanfi.bookstoreserviceapi.dto.response.BookResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.GenericMessageResponse;
import id.izzanfi.bookstoreserviceapi.dto.response.UserDetailsResponse;
import id.izzanfi.bookstoreserviceapi.exception.DataNotFoundException;
import id.izzanfi.bookstoreserviceapi.model.Book;
import id.izzanfi.bookstoreserviceapi.model.User;
import id.izzanfi.bookstoreserviceapi.repository.BookJpaRepository;
import id.izzanfi.bookstoreserviceapi.repository.UserJpaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users/")
public class UserController {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/details")
    public ResponseEntity<?> getLoggedInUser(Authentication authentication) {
        CustomUserDetails authPrincipal = (CustomUserDetails) authentication.getPrincipal();
        if (authPrincipal != null) {
            List<Book> booksOwned = bookJpaRepository.findBooksByUser(authPrincipal.getUserId())
                    .orElse(new ArrayList<>());
            List<BookResponse> ownedBooks = booksOwned.stream()
                    .map(this::convertToBookResponse).collect(Collectors.toList());
            User fetchedUser = userJpaRepository.findById(authPrincipal.getUserId())
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("User with id: %d cannot be found!", authPrincipal.getUserId())));

            return ResponseEntity.ok(new UserDetailsResponse(
                    authPrincipal.getUserId(),
                    authPrincipal.getUsername(),
                    authPrincipal.getUserEmail(),
                    fetchedUser.getUserAddress(),
                    fetchedUser.getUserPhoneNumber(),
                    fetchedUser.getUserBio(),
                    ownedBooks
            ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "No user is logged on!"));
        }
    }

    private BookResponse convertToBookResponse(Book bookEntity) {
        BookResponse response = new BookResponse();
        response.setBookId(bookEntity.getBookId());
        response.setIsbn(bookEntity.getBookIsbn());
        response.setTitle(bookEntity.getBookTitle());
        response.setAuthor(bookEntity.getBookAuthor());
        response.setGenre(bookEntity.getBookGenre());

        return response;
    }

    @PutMapping("/details")
    public ResponseEntity<?> updateLoggedInUser(Authentication authentication, @Valid @RequestBody UpdateUserRequest requestBody) {
        CustomUserDetails authPrincipal = (CustomUserDetails) authentication.getPrincipal();
        if (authPrincipal != null) {
            User currentUser = userJpaRepository.findById(authPrincipal.getUserId()).orElseThrow(
                    () -> new DataNotFoundException(
                            String.format("User with id: %d cannot be found!", authPrincipal.getUserId())));

            if ((requestBody.getOwnedBooksIds() != null) && !(requestBody.getOwnedBooksIds().isEmpty())) {
                // insert new owned books id from request
                List<Book> bookData = bookJpaRepository.findAllById(requestBody.getOwnedBooksIds());
                currentUser.setUserBooks(new HashSet<>(bookData));
            }

            // request payload checking
            if ((requestBody.getUsername() != null) && !(requestBody.getUsername().isEmpty())) {
                currentUser.setUserName(requestBody.getUsername());
            }

            if ((requestBody.getUserEmail() != null) && !(requestBody.getUserEmail().isEmpty())) {
                currentUser.setUserEmail(requestBody.getUserEmail());
            }

            if ((requestBody.getUserPassword() != null) && !(requestBody.getUserPassword().isEmpty())) {
                currentUser.setUserPassword(passwordEncoder.encode(requestBody.getUserPassword()));
            }

            if ((requestBody.getUserAddress() != null) && !(requestBody.getUserAddress().isEmpty())) {
                currentUser.setUserAddress(requestBody.getUserAddress());
            }

            if ((requestBody.getUserBio() != null) && !(requestBody.getUserBio().isEmpty())) {
                currentUser.setUserBio(requestBody.getUserBio());
            }

            if ((requestBody.getUserPhoneNumber() != null) && !(requestBody.getUserPhoneNumber().isEmpty())) {
                currentUser.setUserPhoneNumber(requestBody.getUserPhoneNumber());
            }

            // Saving the updated data to db
            User updatedUserData = userJpaRepository.save(currentUser);
            return ResponseEntity.ok(new UserDetailsResponse(
                    updatedUserData.getUserId(),
                    updatedUserData.getUserName(),
                    updatedUserData.getUserEmail(),
                    updatedUserData.getUserAddress(),
                    updatedUserData.getUserPhoneNumber(),
                    updatedUserData.getUserBio(),
                    updatedUserData.getUserBooks().stream().map(this::convertToBookResponse).toList()
            ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new GenericMessageResponse(false, "No user is logged on!"));
        }
    }

    /**
     * Superuser-only endpoints
     */

    @DeleteMapping("/superuser/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("User with id: %d cannot be found!", userId)));

        log.info(String.format("[/superuser/%d] User Deletion (Superuser-only) endpoint invoked!", userId));
        userJpaRepository.delete(user);
        return ResponseEntity.ok(new GenericMessageResponse(true,
                String.format("User with id: %d has been deleted!", userId)));
    }

    @GetMapping("/superuser/userList")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public List<User> getAllUsers() {
        log.info("[/superuser/userList] User Get All Data (Superuser-only) endpoint invoked!");
        return userJpaRepository.findAll();
    }

    @GetMapping("/superuser/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId) {
        log.info(String.format("[/superuser/%d] User Fetching (Superuser-only) endpoint invoked!", userId));
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id: %d cannot be found!", userId)));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/superuser/createUser")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    @Deprecated
    public User createNewUser(@RequestBody User user) {
        log.info("[/superuser/createUser] User Create Data (Superuser-only) endpoint invoked!");
        return userJpaRepository.save(user);
    }

    @PutMapping("/superuser/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    @Deprecated
    public ResponseEntity<User> updateUserData(@PathVariable("id") Long userId, @RequestBody User userData) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id: %d cannot be found!", userId)));

        // validasi
        if (!userData.getUserName().isEmpty()) {
            user.setUserName(userData.getUserName());
        }

        if (!userData.getUserAddress().isEmpty()) {
            user.setUserAddress(userData.getUserAddress());
        }

        if (!userData.getUserPhoneNumber().isEmpty()) {
            user.setUserPhoneNumber(userData.getUserPhoneNumber());
        }

        if (!userData.getUserBio().isEmpty()) {
            user.setUserBio(userData.getUserBio());
        }

        log.info(String.format("[/superuser/%d] User Update (Superuser-only) endpoint invoked!", user.getUserId()));
        User updatedUserData = userJpaRepository.save(user);
        return ResponseEntity.ok(updatedUserData);
    }
}
