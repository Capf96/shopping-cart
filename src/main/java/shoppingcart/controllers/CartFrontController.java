package shoppingcart.controllers;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import shoppingcart.dataHandlers.ProductHandler;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.services.JSessionId;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartFrontController {
    @Autowired
    JSessionId jSessionId;

    final private String cartApi = "http://localhost:8080/api/cart/";
    final private String productsApi = "http://localhost:8080/api/products/";
    final private String checkoutApi = "http://localhost:8080/api/checkout/";

    @GetMapping
    public String displayItemsInCart(Model model, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        HttpEntity<ProductsResponse[]> response = restTemplate.exchange(cartApi, HttpMethod.GET, entity, ProductsResponse[].class);

        ProductsResponse[] productsInCart = response.getBody();

        List<ProductHandler> inCart = new ArrayList<>();
        for (ProductsResponse product: productsInCart) {
            HttpEntity<ProductImagesResponse[]> responseImage =
                    restTemplate.exchange(productsApi + product.getProductId() + "/images/" , HttpMethod.GET, entity, ProductImagesResponse[].class);

            List<ProductImagesResponse> imagesResponses = Arrays.asList(responseImage.getBody());

            ProductHandler toAdd = ProductHandler.builder()
                    .productId(product.getProductId())
                    .seller(product.getSeller())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .images(imagesResponses)
                    .quantity(product.getQuantity())
                    .build();

            inCart.add(toAdd);
        }

        model.addAttribute("inCart", inCart);

        return "cartPage";
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateCart(@RequestParam("ids") Long[] ids, @RequestParam("quantities") Integer[] quantities) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = jSessionId.getJSessionId();


        int i = 0;
        while (i < ids.length) {
            Long productId = ids[i];
            Integer quantity = quantities[i];

            Map<String, Integer> body = new HashMap<>();
            body.put("quantity", quantity);

            HttpEntity entity = new HttpEntity(body, headers);

            HttpEntity<ProductsStoreResponse> response = restTemplate.exchange(cartApi + productId + "/", HttpMethod.PATCH, entity, ProductsStoreResponse.class);

            i = i + 1;
        }

        return "redirect:/cart";
    }

    @GetMapping("/{productId}/remove")
    public String removeFromCart(@PathVariable Long productId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        restTemplate.exchange(cartApi + productId + "/", HttpMethod.DELETE, entity, void.class);

        return "redirect:/cart";
    }

    @PostMapping(value = "/addProduct", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addToCart(@RequestParam("id") Long productId, @RequestParam("quantity") Integer quantity) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        Map<String, Object> body = new HashMap<>();
        body.put("productId", productId);
        body.put("quantity", quantity);

        HttpEntity entity = new HttpEntity(body, headers);

        ResponseEntity<CartResponse> response = restTemplate.exchange(cartApi, HttpMethod.PUT, entity, CartResponse.class);

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<CartResponse> response = restTemplate.exchange(checkoutApi, HttpMethod.PUT, entity, CartResponse.class);

        return "redirect:/userInfo";
    }
}
