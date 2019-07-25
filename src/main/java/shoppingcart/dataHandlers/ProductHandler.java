package shoppingcart.dataHandlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingcart.responses.ProductImagesResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHandler {
    private Long productId;

    private String name;

    private String description;

    private String seller;

    private Double price;

    private Integer quantity;

    private List<ProductImagesResponse> images;
}
