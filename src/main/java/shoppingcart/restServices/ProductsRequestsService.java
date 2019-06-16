package shoppingcart.restServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shoppingcart.models.AppUser;
import shoppingcart.models.Products;
import shoppingcart.models.UserRole;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaProductsRepository;
import shoppingcart.repository.JpaUserRoleRepository;
import shoppingcart.requests.ProductsPatchRequest;
import shoppingcart.requests.ProductsRequest;
import shoppingcart.responses.ProductsResponse;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductsRequestsService {
    @Autowired
    JpaProductsRepository productsRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaUserRoleRepository userRoleRepo;

    // GET METHODS

    public List<ProductsResponse> manageGet() {
        List<Products> products = productsRepo.findAll();

        List<ProductsResponse> responseList = new ArrayList<>();
        for (Products product: products) {
            ProductsResponse toAdd = ProductsResponse.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .seller(product.getSeller().getUsername())
                    .price(product.getPrice())
                    .visible(product.getVisible())
                    .quantity(product.getQuantity())
                    .build();
            responseList.add(toAdd);
        }

        return responseList;
    }

    public ProductsResponse manageGetByProductId(Long productId) {
        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        return ProductsResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .seller(product.getSeller().getUsername())
                .visible(product.getVisible())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    // POST

    public ProductsResponse managePost(ProductsRequest product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser") throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        User user = (User) authentication.getPrincipal();
        UserRole isSeller = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(user.getUsername(), "ROLE_SELLER");

        if (isSeller == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        AppUser seller = appUserRepo.findByUsername(user.getUsername());
        if (seller == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found");

        Products newProduct = Products.builder()
                .name(product.getName())
                .description(product.getDescription())
                .seller(seller)
                .visible(product.getVisible())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        Products saved = productsRepo.save(newProduct);

        return ProductsResponse.builder()
                .productId(saved.getProductId())
                .name(saved.getName())
                .description(saved.getDescription())
                .seller(saved.getSeller().getUsername())
                .visible(saved.getVisible())
                .price(saved.getPrice())
                .quantity(saved.getQuantity())
                .build();
    }

    // PATCH

    public ProductsResponse managePatch(Long productId, ProductsPatchRequest product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser") throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        User user = (User) authentication.getPrincipal();
        Products toUpdate = productsRepo.findByProductId(productId);
        if (toUpdate == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        if (!user.getUsername().equals(toUpdate.getSeller().getUsername())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Products updated = Products.builder().productId(toUpdate.getProductId())
                .name((product.getName() != null)? product.getName(): toUpdate.getName())
                .description((product.getDescription() != null)? product.getDescription(): toUpdate.getDescription())
                .seller(toUpdate.getSeller())
                .visible((product.getVisible() != null)? product.getVisible(): toUpdate.getVisible())
                .price((product.getPrice() != null)? product.getPrice(): toUpdate.getPrice())
                .quantity((product.getQuantity() != null)? product.getQuantity(): toUpdate.getQuantity())
                .build();

        Products saved = productsRepo.save(updated);

        return ProductsResponse.builder().productId(saved.getProductId())
                .name(saved.getName())
                .description(saved.getDescription())
                .seller(saved.getSeller().getUsername())
                .visible(saved.getVisible())
                .price(saved.getPrice())
                .quantity(saved.getQuantity())
                .build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> manageDelete(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser") throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        User user = (User) authentication.getPrincipal();

        Products toDelete = productsRepo.findByProductId(productId);
        if (toDelete == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        UserRole isAdmin = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(user.getUsername(), "ROLE_ADMIN");
        if (isAdmin == null || !user.getUsername().equals(toDelete.getSeller().getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        productsRepo.delete(toDelete);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
