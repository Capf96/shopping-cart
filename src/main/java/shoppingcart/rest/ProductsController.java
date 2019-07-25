package shoppingcart.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingcart.requests.ProductsPatchRequest;
import shoppingcart.requests.ProductsRequest;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.restServices.ProductsRequestsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Api(description="Operations pertaining to products in Shopping Cart API", tags = "Products")
@RestController
@RequestMapping("/api/products/")
public class ProductsController {
    @Autowired
    ProductsRequestsService productsRequestsService;

    @ApiOperation(value = "View a list of available products in store", response = ProductsResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
    })
    @GetMapping
    public List<ProductsResponse> getProducts(HttpServletRequest request) {
        Enumeration<String> hearderNames = request.getHeaderNames();
        while(hearderNames.hasMoreElements())
        {
            String headerName = hearderNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }

        return productsRequestsService.manageGet();
    }

    @ApiOperation(value = "View the requested product", response = ProductsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping("/{productId}/")
    public ProductsResponse getProductByProductId(@PathVariable Long productId) {
        return productsRequestsService.manageGetByProductId(productId);
    }

    @ApiOperation(value = "Create a new product", response = ProductsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @PostMapping
    public ProductsResponse createProduct(@Valid @RequestBody ProductsRequest product) {
        return productsRequestsService.managePost(product);
    }

    @ApiOperation(value = "Change a product information", response = ProductsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully modified product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @PatchMapping("/{productId}/")
    public ProductsResponse updateProduct(@Valid @RequestBody ProductsPatchRequest product, @PathVariable Long productId) {
        return productsRequestsService.managePatch(productId, product);
    }

    @ApiOperation(value = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{productId}/")
    public void deleteProduct(@PathVariable Long productId) {
        productsRequestsService.manageDelete(productId);
    }
}
