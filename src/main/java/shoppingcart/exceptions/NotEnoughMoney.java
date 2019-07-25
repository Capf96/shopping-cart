package shoppingcart.exceptions;

import lombok.Data;

public class NotEnoughMoney extends RuntimeException {
    private Double money;
    private Double totalPrice;

    public NotEnoughMoney(String details, Double money, Double price) {
        super(details);
        this.money = money;
        this.totalPrice = price;
    }

    public Double getMoney() {
        return money;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
}
