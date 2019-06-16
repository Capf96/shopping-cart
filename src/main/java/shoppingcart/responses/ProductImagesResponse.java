package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImagesResponse {
    private Long productImageId;
    private Long productId;
    private String path;
}
