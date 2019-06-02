package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRoleResponse {
    private Long userRoleId;
    private Long appUserId;
    private Long appRoleId;
}