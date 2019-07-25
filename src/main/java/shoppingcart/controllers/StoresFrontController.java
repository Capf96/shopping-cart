package shoppingcart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import shoppingcart.dataHandlers.ProductHandler;
import shoppingcart.dataHandlers.StoreHandler;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.responses.StoreResponse;
import shoppingcart.services.JSessionId;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public class StoresFrontController {
    @Autowired
    JSessionId jSessionId;

    private final String productsApi = "http://localhost:8080/api/products/";
    private final String storesApi = "http://localhost:8080/api/stores/";
    private final String usersApi = "http://localhost:8080/api/users/";

    @GetMapping("/stores")
    public String getStores(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity headers = new HttpEntity(jSessionId.getJSessionId());
        HttpEntity<StoreResponse[]> response = restTemplate.exchange(storesApi, HttpMethod.GET, headers, StoreResponse[].class);
        if (response.hasBody()) {
            List<StoreResponse> storesResponse = Arrays.asList(response.getBody());

            List<StoreHandler> stores = new ArrayList<>();
            for (StoreResponse store : storesResponse) {
                List<ProductHandler> inStore = new ArrayList<>();
                for (ProductsStoreResponse product: store.getProducts()) {
                    HttpEntity<ProductImagesResponse[]> responseImage =
                            restTemplate.exchange(productsApi + product.getProductId() + "/images/" , HttpMethod.GET, headers, ProductImagesResponse[].class);

                    List<ProductImagesResponse> imagesResponses = Arrays.asList(responseImage.getBody());

                    ProductHandler toAdd = ProductHandler.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .images(imagesResponses)
                            .seller(store.getUsername())
                            .build();

                    inStore.add(toAdd);
                }
                StoreHandler addStore = StoreHandler.builder()
                        .seller(store.getUsername())
                        .products(inStore)
                        .build();

                stores.add(addStore);
            }

            model.addAttribute("stores", stores);

        }

        return "storesPage";
    }

    @GetMapping("/users/{username}/store")
    public String getSingleStore(Model model, @PathVariable String username) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity headers = new HttpEntity(jSessionId.getJSessionId());
        HttpEntity<StoreResponse> response = restTemplate.exchange(usersApi + username + "/store/", HttpMethod.GET, headers, StoreResponse.class);

        StoreResponse storeResponse = response.getBody();

        List<ProductHandler> inStore = new ArrayList<>();
        for (ProductsStoreResponse product: storeResponse.getProducts()) {
            HttpEntity<ProductImagesResponse[]> responseImage =
                    restTemplate.exchange(productsApi + product.getProductId() + "/images/" , HttpMethod.GET, headers, ProductImagesResponse[].class);

            List<ProductImagesResponse> imagesResponses = Arrays.asList(responseImage.getBody());

            ProductHandler toAdd = ProductHandler.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .images(imagesResponses)
                    .seller(storeResponse.getUsername())
                    .build();

            inStore.add(toAdd);
        }

        StoreHandler store = StoreHandler.builder()
                .seller(storeResponse.getUsername())
                .products(inStore)
                .build();

        model.addAttribute("store", store);

        return "storePage";
    }

}
