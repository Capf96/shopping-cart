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
public class AppRoleRequest {
    @NotNull(message = "The role name is a required field")
    private String roleName;

    private Long appRoleId;
}