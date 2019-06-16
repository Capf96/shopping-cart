package shoppingcart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingcart.requests.CartRequest;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.restServices.CartRequestsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    CartRequestsService cartRequestsService;

    @GetMapping
    public List<ProductsStoreResponse> manageGet() {
        return cartRequestsService.getProductsInCart();
    }

    @PutMapping
    public CartResponse managePut(@Valid @RequestBody CartRequest cartRequest) {
        return cartRequestsService.addToCart(cartRequest);
    }

    @DeleteMapping("/{productId}/")
    public ResponseEntity <HttpStatus> manageDelete(@PathVariable Long productId) {
        return cartRequestsService.deleteFromCart(productId);
    }

    @PatchMapping("/{productId}/")
    public CartResponse managePatch(@PathVariable Long productId, @Valid @RequestBody CartRequest cartRequest) {
        return cartRequestsService.updateQuantity(productId, cartRequest);
    }
}
