package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Integer enabled;
    private Double money;
}