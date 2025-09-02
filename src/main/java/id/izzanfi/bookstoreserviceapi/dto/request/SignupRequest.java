package id.izzanfi.bookstoreserviceapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank
    @Email
    private String userEmail;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> userRoles;
}
