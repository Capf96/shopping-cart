package shoppingcart.dataHandlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesHandler {
    private String username;
    private boolean buyer;
    private boolean seller;
    private boolean admin;
}
