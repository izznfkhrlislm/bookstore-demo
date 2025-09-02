package id.izzanfi.bookstoreserviceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {

    private long userId;
    private String username;
    private String email;
    private String address;
    private String phoneNumber;
    private String userBio;
    private List<BookResponse> booksOwned;
}
