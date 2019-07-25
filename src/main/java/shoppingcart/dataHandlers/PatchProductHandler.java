package shoppingcart.dataHandlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchProductHandler {
    private Long productId;
    private String name;
    private String description;
    private String seller;
    private Double price;
    private boolean visible;
    private Integer quantity;
}
