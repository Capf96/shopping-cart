package shoppingcart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shoppingcart.models.Cart;
import shoppingcart.models.CartIdentity;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaCartRepository;
import shoppingcart.repository.JpaProductsRepository;
import shoppingcart.responses.StoreResponse;
import shoppingcart.restServices.StoresRequestsService;

import java.util.List;

@RestController
@RequestMapping
public class StoresController {
    @Autowired
    StoresRequestsService storesRequestsService;

    @Autowired
    JpaCartRepository cartRepo;

    @Autowired
    JpaAppUserRepository userRepo;

    @Autowired
    JpaProductsRepository productsRepo;

    @GetMapping("/api/stores/")
    public List<StoreResponse> getStores() {
        return storesRequestsService.manageGet();
    }

    @GetMapping("/api/users/{username}/store/")
    public StoreResponse getUserStore(@PathVariable String username) {
        return storesRequestsService.manageGetUserStore(username);
    }
}
