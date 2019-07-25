package shoppingcart.restServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;
import shoppingcart.models.*;
import shoppingcart.repository.*;
import shoppingcart.requests.ProductsPatchRequest;
import shoppingcart.requests.ProductsRequest;
import shoppingcart.responses.ProductsResponse;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductsRequestsService {
    @Autowired
    JpaProductsRepository productsRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaUserRoleRepository userRoleRepo;

    @Autowired
    JpaTrustRepository trustRepo;

    @Autowired
    JpaProductImagesRepository imagesRepo;

    @Autowired
    ProductImagesRequestsService productImagesRequestsService;

    // GET METHODS

    public List<ProductsResponse> manageGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean checkVisibility = false;
        boolean admin = false;
        AppUser user = null;
        if (authentication.getPrincipal() != "anonymousUser") {
            user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());
            checkVisibility = true;
            if (userRoleRepo.findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(user.getUsername(), "ROLE_ADMIN") != null) {
                admin = true;
            }
        }

        List<Products> products = productsRepo.findAll();

        List<ProductsResponse> responseList = new ArrayList<>();
        for (Products product: products) {
            ProductsResponse.ProductsResponseBuilder toAdd = ProductsResponse.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .seller(product.getSeller().getUsername())
                    .price(product.getPrice())
                    .quantity(product.getQuantity());
            if (admin || (checkVisibility && product.getSeller().getUsername().equals(user.getUsername()))) {
                responseList.add(toAdd.visible(product.getVisible()).build());
            } else if (checkVisibility &&
                    trustRepo.findByTrust_Truster_UsernameAndTrust_Trustee_Username(product.getSeller().getUsername(), user.getUsername()) != null) {
                responseList.add(toAdd.build());
            } else if (product.getVisible()) {
                responseList.add(toAdd.build());
            }
        }

        return responseList;
    }

    public ProductsResponse manageGetByProductId(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean checkVisibility = false;
        boolean admin = false;
        AppUser user = null;
        if (authentication.getPrincipal() != "anonymousUser") {
            user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());
            checkVisibility = true;
            if (userRoleRepo.findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(user.getUsername(), "ROLE_ADMIN") != null) {
                admin = true;
            }
        }

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        if (checkVisibility && !product.getVisible() && !product.getSeller().getUsername().equals(user.getUsername())) {
            Trust isTrusted = trustRepo.findByTrust_Truster_UsernameAndTrust_Trustee_Username(product.getSeller().getUsername(), user.getUsername());
            if (isTrusted == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        } else if (!checkVisibility && !product.getVisible()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        ProductsResponse.ProductsResponseBuilder builder = ProductsResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .seller(product.getSeller().getUsername())
                .price(product.getPrice())
                .quantity(product.getQuantity());

        if (admin || (checkVisibility && product.getSeller().getUsername().equals(user.getUsername())))
            return builder.visible(product.getVisible()).build();

        return builder.build();
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

    public void manageDelete(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser") throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        User user = (User) authentication.getPrincipal();

        Products toDelete = productsRepo.findByProductId(productId);
        if (toDelete == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        UserRole isAdmin = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(user.getUsername(), "ROLE_ADMIN");
        if (isAdmin == null || !user.getUsername().equals(toDelete.getSeller().getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        List<ProductImages> images = imagesRepo.findByProduct_ProductId(toDelete.getProductId());

        for (ProductImages image : images) {
            productImagesRequestsService.deleteImage(image.getProduct().getProductId(), image.getProductImageId());
        }

        productsRepo.delete(toDelete);
    }

}
