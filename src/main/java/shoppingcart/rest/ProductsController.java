package shoppingcart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingcart.requests.ProductsPatchRequest;
import shoppingcart.requests.ProductsRequest;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.restServices.ProductsRequestsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products/")
public class ProductsController {
    @Autowired
    ProductsRequestsService productsRequestsService;

    @GetMapping
    public List<ProductsResponse> getProducts() {
        return productsRequestsService.manageGet();
    }

    @GetMapping("/{productId}/")
    public ProductsResponse getProductByProductId(@PathVariable Long productId) {
        return productsRequestsService.manageGetByProductId(productId);
    }

    @PostMapping
    public ProductsResponse createProduct(@Valid @RequestBody ProductsRequest product) {
        return productsRequestsService.managePost(product);
    }

    @PatchMapping("/{productId}/")
    public ProductsResponse updateProduct(@Valid @RequestBody ProductsPatchRequest product, @PathVariable Long productId) {
        return productsRequestsService.managePatch(productId, product);
    }

    @DeleteMapping("/{productId}/")
    public ResponseEntity <HttpStatus> deleteProduct(@PathVariable Long productId) {
        return productsRequestsService.manageDelete(productId);
    }
}
