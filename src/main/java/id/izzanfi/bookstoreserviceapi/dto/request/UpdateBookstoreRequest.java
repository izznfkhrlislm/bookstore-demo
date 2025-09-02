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
public class UpdateBookstoreRequest {

    private String bookstoreName;
    private String bookstoreAddress;
    private String bookstorePhoneNumber;
    private Boolean isActive;
    private Set<UpdateBookStockRequest> bookStockRequest;
}
