package shoppingcart.dataHandlers;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreHandler {
    private String seller;
    private List<ProductHandler> products;
}
