package id.izzanfi.bookstoreserviceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String genre;
}
