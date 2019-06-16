package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PurchaseResponse {
    private Double totalPrice;
    private Date dateOfPurchase;
    private List<ProductsStoreResponse> products;
}
