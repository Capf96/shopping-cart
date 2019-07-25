package shoppingcart.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserPatchRequest {
    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Min(0)
    private Double money;

    private Integer enabled;
}
