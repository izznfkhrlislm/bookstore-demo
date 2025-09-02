package id.izzanfi.bookstoreserviceapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookStockRequest {

    private Long bookId;
    private Long bookstoreId;
    private Integer bookStock = 0;
}
