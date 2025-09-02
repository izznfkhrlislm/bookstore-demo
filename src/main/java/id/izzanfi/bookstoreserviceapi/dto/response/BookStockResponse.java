package id.izzanfi.bookstoreserviceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookStockResponse {

    private Long bookId;
    private Long bookstoreId;
    private int bookStock = 0;
    private String bookTitle;
    private String bookAuthor;
    private String isbn;
    private String bookstoreName;
    private String bookstoreAddress;
    private String bookstorePhoneNumber;
}
