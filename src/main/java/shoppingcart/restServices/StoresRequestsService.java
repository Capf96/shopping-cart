package shoppingcart.restServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import shoppingcart.repository.JpaTrustRepository;
import shoppingcart.repository.JpaUserRoleRepository;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.responses.StoreResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoresRequestsService {
    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaProductsRepository productsRepo;

    @Autowired
    JpaTrustRepository trustRepo;

    @Autowired
    JpaUserRoleRepository userRoleRepo;

    public List<StoreResponse> manageGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean checkVisible = authentication.getPrincipal() != "anonymousUser";

        AppUser user = null;
        if (checkVisible) {
            user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());
        }

        List<AppUser> sellers = appUserRepo.findByRoleName("ROLE_SELLER");
        List<StoreResponse> stores = new ArrayList<>();
        for (AppUser seller: sellers) {
            List<Products> sellerProducts = productsRepo.findBySeller(seller);
            List<ProductsStoreResponse> store = new ArrayList<>();
            boolean add = false;
            if (checkVisible) {
                add = trustRepo.findByTrusterAndTrustee(seller, user) != null || user.getUsername().equals(seller.getUsername());
            }
            for (Products product: sellerProducts) {
                if (add || product.getVisible()) {
                    ProductsStoreResponse toAdd = ProductsStoreResponse.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    store.add(toAdd);
                }
            }
            // TODO: Verificar si devolver o no una tienda vacia
//            if (!store.isEmpty()) {
//                stores.add(StoreResponse.builder()
//                        .username(seller.getUsername())
//                        .products(store)
//                        .build());
//            }
            stores.add(StoreResponse.builder()
                    .username(seller.getUsername())
                    .products(store)
                    .build());
        }
        return stores;
    }

    public StoreResponse manageGetUserStore(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean checkVisible = authentication.getPrincipal() != "anonymousUser";

        AppUser user = null;
        if (checkVisible) {
            user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());
        }

        AppUser seller = appUserRepo.findByUsername(username);
        if (seller == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        // TODO: Verificar si el precondition failed es correcto
        UserRole isSeller = userRoleRepo.findByUsernameAndRoleName(username, "ROLE_SELLER");
        if (isSeller == null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "User is not seller");

        List<Products> sellerProducts = productsRepo.findBySeller(seller);
        List<ProductsStoreResponse> store = new ArrayList<>();
        boolean add = false;
        if (checkVisible) {
            add = trustRepo.findByTrusterAndTrustee(seller, user) != null || user.getUsername().equals(seller.getUsername());
        }
        for (Products product: sellerProducts) {
            if (add || product.getVisible()) {
                ProductsStoreResponse toAdd = ProductsStoreResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .build();
                store.add(toAdd);
            }
        }
        return StoreResponse.builder()
                .username(seller.getUsername())
                .products(store)
                .build();
    }
}
