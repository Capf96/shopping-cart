package shoppingcart.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.restServices.ProductImagesRequestsService;

import java.io.IOException;
import java.util.List;

@Api(description="Operations pertaining to products in Shopping Cart API", tags = "Products")
@RestController
@RequestMapping("/api/products/{productId}/images/")
public class ProductImagesController {
    @Autowired
    ProductImagesRequestsService productImagesRequestsService;

    @ApiOperation(value = "Get a product images", response = ProductImagesResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping
    public List<ProductImagesResponse> manageGet(@PathVariable Long productId) {
        return productImagesRequestsService.getProductImages(productId);
    }

    @ApiOperation(value = "Add a product to your cart", response = ProductImagesResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added an image to the product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 415, message = "The file extension is not supported")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductImagesResponse managePost(@PathVariable Long productId, @RequestParam("imageFile") MultipartFile imageFile) {
        return productImagesRequestsService.saveImage(productId, imageFile);
    }

    @ApiOperation(value = "Add a product to your cart")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted an image from the product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{productImageId}/")
    public void manageDelete(@PathVariable Long productId, @PathVariable Long productImageId) {
        productImagesRequestsService.deleteImage(productId, productImageId);
    }
}
