package shoppingcart.requests;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppRoleRequest {
    @NotNull(message = "The role name is a required field")
    private String roleName;

    private Long appRoleId;
}