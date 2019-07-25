package shoppingcart.requests;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRequest {
    @NotNull(message = "The username is a required field")
    private String username;

    @NotNull(message = "The password is a required field")
    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Double money;
}