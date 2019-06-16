package shoppingcart.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserPatchRequest {
    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Integer enabled;

    private Double money;
}
