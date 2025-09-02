package id.izzanfi.bookstoreserviceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookstoreResponse {

    private Long bookstoreId;
    private String bookstoreName;
    private String bookstoreAddress;
    private String bookstorePhoneNo;
    private Boolean isActive;
    private List<BookStockResponse> bookstoreStockData = new ArrayList<>();
}
