package id.izzanfi.bookstoreserviceapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private String username;
    private String userPassword;
    private String userEmail;
    private String userAddress;
    private String userPhoneNumber;
    private String userBio;
    private Set<Long> ownedBooksIds = new HashSet<>();
}
