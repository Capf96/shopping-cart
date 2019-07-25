package shoppingcart.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotEnoughInInventoryResponse {
    private String details;
    private List<Long> productIds;
    private List<String> productNames;
}
