package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppRoleResponse {
    private Long roleId;
    private String roleName;
}