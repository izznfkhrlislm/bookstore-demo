package id.izzanfi.bookstoreserviceapi.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericMessageResponse {

    @NotBlank
    private Boolean isSuccess;

    @NotBlank
    private String message;
}
