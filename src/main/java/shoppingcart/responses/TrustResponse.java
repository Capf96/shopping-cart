package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

import shoppingcart.models.AppUser;

@Data
@Builder
public class TrustResponse {
    private Long trustId;
    private String truster;
    private String trustee;
}
