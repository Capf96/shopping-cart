package shoppingcart.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingcart.exceptions.NotEnoughInInventory;
import shoppingcart.exceptions.NotEnoughInInventoryResponse;
import shoppingcart.exceptions.NotEnoughMoney;
import shoppingcart.exceptions.NotEnoughMoneyResponse;
import shoppingcart.requests.CartRequest;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.responses.PurchaseResponse;
import shoppingcart.restServices.CartRequestsService;

import javax.validation.Valid;
import java.util.List;

@Api(description="Operations pertaining to the cart in Shopping Cart API", tags = "Cart")
@RestController
@RequestMapping("/api/")
public class CartController {
    @Autowired
    CartRequestsService cartRequestsService;

    @ApiOperation(value = "View the list of products in cart", response = ProductsStoreResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of products in cart"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/cart/")
    public List<ProductsResponse> manageGet() {
        return cartRequestsService.getProductsInCart();
    }

    @ApiOperation(value = "Add a product to your cart", response = CartResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added a product to cart"),
            @ApiResponse(code = 400, message = "The request was badly formed"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 412, message = "The cart already has that product")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/cart/")
    public CartResponse managePut(@Valid @RequestBody CartRequest cartRequest) {
        return cartRequestsService.addToCart(cartRequest);
    }

    @ApiOperation(value = "Delete a product from your cart")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully removed product from cart"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart/{productId}/")
    public void manageDelete(@PathVariable Long productId) {
        cartRequestsService.deleteFromCart(productId);
    }

    @ApiOperation(value = "Modify the quantity of a product in cart", response = CartResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added a product to cart"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @PatchMapping("/cart/{productId}/")
    public CartResponse managePatch(@PathVariable Long productId, @Valid @RequestBody CartRequest cartRequest) {
        return cartRequestsService.updateQuantity(productId, cartRequest);
    }

    @ApiOperation(value = "Buy the products from your cart", response = PurchaseResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully completed transaction"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 412, message = "Not enough money available or not enough products in store to satisfy request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/checkout/")
    public PurchaseResponse manageCheckout() {
        return cartRequestsService.buyProductsInCart();
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(NotEnoughInInventory.class)
    public NotEnoughInInventoryResponse handleNotEnoughInInventory(NotEnoughInInventory ex) {
        return NotEnoughInInventoryResponse.builder()
                .details(ex.getMessage())
                .productIds(ex.getProductIds())
                .productNames(ex.getProductNames())
                .build();
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(NotEnoughMoney.class)
    public NotEnoughMoneyResponse handleNotEnoughMoney(NotEnoughMoney ex) {
        return NotEnoughMoneyResponse.builder()
                .details(ex.getMessage())
                .money(ex.getMoney())
                .totalPrice(ex.getTotalPrice())
                .build();
    }
}
