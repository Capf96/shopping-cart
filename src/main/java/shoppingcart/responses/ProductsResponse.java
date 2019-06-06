package shoppingcart.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingcart.models.AppUser;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsResponse {
    private Long productId;

    private String name;

    private String description;

    private String seller;

    private Double price;

    private Boolean visible;

    private Integer quantity;

}