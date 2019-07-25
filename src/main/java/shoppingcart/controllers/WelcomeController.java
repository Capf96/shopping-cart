package shoppingcart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import shoppingcart.dataHandlers.ProductHandler;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.services.JSessionId;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = {"/", "/welcome"})
public class WelcomeController {
    @Autowired
    JSessionId jSessionId;

    private final String productsApi = "http://localhost:8080/api/products/";

    @GetMapping
    public String welcomePage(Model model) {

        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<ProductsResponse[]> response = restTemplate.exchange(productsApi, HttpMethod.GET, entity, ProductsResponse[].class);

        List<ProductHandler> products = new ArrayList<>();
        if (response.hasBody()) {
            for (ProductsResponse r: response.getBody()) {
                HttpEntity<ProductImagesResponse[]> responseImage =
                        restTemplate.exchange(productsApi + r.getProductId() + "/images/" , HttpMethod.GET, entity, ProductImagesResponse[].class);

                List<ProductImagesResponse> imagesResponses = Arrays.asList(responseImage.getBody());

                ProductHandler toAdd = ProductHandler.builder()
                        .productId(r.getProductId())
                        .name(r.getName())
                        .description(r.getDescription())
                        .price(r.getPrice())
                        .quantity(r.getQuantity())
                        .seller(r.getSeller())
                        .images(imagesResponses)
                        .build();

                products.add(toAdd);

            }
        }

        model.addAttribute("products", products);

        return "welcomePage";
    }
}
