package id.izzanfi.bookstoreserviceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    private String authToken;
    private String type = "Bearer";
    private Long userId;
    private String userName;
    private String userEmail;
    private List<String> userRoles;

    public JwtAuthResponse(String jwtToken, Long userId, String username, String userEmail, List<String> userRoles) {
        this.authToken = jwtToken;
        this.userId = userId;
        this.userName = username;
        this.userEmail = userEmail;
        this.userRoles = userRoles;
    }
}
