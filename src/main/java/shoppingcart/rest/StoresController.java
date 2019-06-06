package shoppingcart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingcart.responses.StoreResponse;
import shoppingcart.restServices.StoresRequestsService;

import java.util.List;

@RestController
@RequestMapping
public class StoresController {
    @Autowired
    StoresRequestsService storesRequestsService;

    @GetMapping("/api/stores/")
    public List<StoreResponse> getStores() {
        return storesRequestsService.manageGet();
    }

    @GetMapping("/api/users/{username}/store/")
    public StoreResponse getUserStore(@PathVariable String username) {
        return storesRequestsService.manageGetUserStore(username);
    }
}
