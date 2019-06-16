package shoppingcart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.restServices.ProductImagesRequestsService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products/{productId}/images/")
public class ProductImagesController {
    @Autowired
    ProductImagesRequestsService productImagesRequestsService;

    @GetMapping
    public List<ProductImagesResponse> manageGet(@PathVariable Long productId) {
        return productImagesRequestsService.getProductImages(productId);
    }

    @PostMapping
    public ProductImagesResponse managePost(@PathVariable Long productId, @RequestParam("imageFile") MultipartFile imageFile) {
        return productImagesRequestsService.saveImage(productId, imageFile);
    }

    @DeleteMapping("/{productImageId}/")
    public ResponseEntity <HttpStatus> manageDelete(@PathVariable Long productId, @PathVariable Long productImageId) {
        return productImagesRequestsService.deleteImage(productId, productImageId);
    }
}
