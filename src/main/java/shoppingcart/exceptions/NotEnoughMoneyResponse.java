package shoppingcart.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotEnoughMoneyResponse {
    private String details;
    private Double money;
    private Double totalPrice;
}
