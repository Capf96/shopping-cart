package shoppingcart.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsStoreResponse {
    private Long productId;

    private String name;

    private String description;

    private Double price;

    private Integer quantity;
}
