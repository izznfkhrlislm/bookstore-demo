package id.izzanfi.bookstoreserviceapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequest {

    private String bookAuthor;
    private String bookTitle;
    private String isbn;
    private String bookGenre;
    private Set<UpdateBookStockRequest> bookStockRequest;
}
