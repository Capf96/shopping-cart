package shoppingcart.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import shoppingcart.models.AppUser;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustResponse {
    private String username;
    private List<String> trustedUsers;
}
