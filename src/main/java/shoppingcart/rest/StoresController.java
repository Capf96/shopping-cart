package shoppingcart.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shoppingcart.models.Cart;
import shoppingcart.models.CartIdentity;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaCartRepository;
import shoppingcart.repository.JpaProductsRepository;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.StoreResponse;
import shoppingcart.restServices.StoresRequestsService;

import java.util.List;

@Api(description="Operations pertaining to stores in Shopping Cart API", tags = "Stores")
@RestController
@RequestMapping("/api/")
public class StoresController {
    @Autowired
    StoresRequestsService storesRequestsService;

    @ApiOperation(value = "Get available stores", response = StoreResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
    })
    @GetMapping("/stores/")
    public List<StoreResponse> getStores() {
        return storesRequestsService.manageGet();
    }

    @ApiOperation(value = "Get an user store", response = CartResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieve store"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping("/users/{username}/store/")
    public StoreResponse getUserStore(@PathVariable String username) {
        return storesRequestsService.manageGetUserStore(username);
    }
}
