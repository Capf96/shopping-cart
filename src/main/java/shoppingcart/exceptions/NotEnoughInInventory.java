package shoppingcart.exceptions;

import java.util.List;


public class NotEnoughInInventory extends RuntimeException{
    private List<Long> productIds;
    private List<String> productNames;

    public NotEnoughInInventory(String details, List<Long> ids, List<String> names) {
        super(details);
        this.productIds = ids;
        this.productNames = names;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public List<String> getProductNames() {
        return productNames;
    }
}
