package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreResponse {
    private String username;
    private List<ProductsStoreResponse> products;
}
