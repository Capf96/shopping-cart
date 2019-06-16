package shoppingcart.restServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;
import shoppingcart.models.*;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaCartRepository;
import shoppingcart.repository.JpaProductsRepository;
import shoppingcart.repository.JpaPurchasesRepository;
import shoppingcart.requests.CartRequest;
import shoppingcart.responses.CartResponse;
import shoppingcart.responses.ProductsStoreResponse;
import shoppingcart.responses.PurchaseResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CartRequestsService {
    // TODO: Only ROLE_BUYER can enter here
    @Autowired
    JpaCartRepository cartRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaProductsRepository productsRepo;

    @Autowired
    JpaPurchasesRepository purchasesRepo;

    // GET

    public List<ProductsStoreResponse> getProductsInCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        List<Cart> items = cartRepo.findByCartId_User(user);

        List<ProductsStoreResponse> response = new ArrayList<>();
        for (Cart item : items) {
            Products product = item.getCartId().getProduct();
            ProductsStoreResponse toAdd = ProductsStoreResponse.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .build();
            response.add(toAdd);
        }

        return response;
    }

    // PATCH

    public CartResponse updateQuantity(Long productId, CartRequest cartPatch) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        CartIdentity cartIdentity = CartIdentity.builder()
                .user(user)
                .product(product)
                .build();

        Cart cart = cartRepo.findByCartId(cartIdentity);
        if (cart == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not in cart");

        Cart updated = Cart.builder()
                .cartId(cartIdentity)
                .quantity(cartPatch.getQuantity())
                .build();

        Cart saved = cartRepo.save(updated);

        return CartResponse.builder()
                .productId(saved.getCartId().getProduct().getProductId())
                .quantity(saved.getQuantity())
                .build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> deleteFromCart(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        CartIdentity cartIdentity = CartIdentity.builder()
                .user(user)
                .product(product)
                .build();

        Cart cart = cartRepo.findByCartId(cartIdentity);
        if (cart == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not in cart");

        cartRepo.delete(cart);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT

    public CartResponse addToCart(CartRequest cart) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        Products product = productsRepo.findByProductId(cart.getProductId());
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        CartIdentity cartIdentity = CartIdentity.builder()
                .user(user)
                .product(product)
                .build();

        Cart inCart = cartRepo.findByCartId(cartIdentity);
        if (inCart != null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Product already in cart");

        Cart newCart = Cart.builder()
                .cartId(cartIdentity)
                .quantity(cart.getQuantity())
                .build();

        Cart saved = cartRepo.save(newCart);

        return CartResponse.builder()
                .productId(saved.getCartId().getProduct().getProductId())
                .quantity(saved.getQuantity())
                .build();
    }

    // PUT CHECKOUT

    @PutMapping("/checkout/")
    public PurchaseResponse buyProductsInCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        List<Cart> inCart = cartRepo.findByCartId_User(user);
        if (inCart.isEmpty()) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "The cart is empty");

        List<ProductsStoreResponse> products = new ArrayList<>();
        Double totalPrice = 0.0;
        for (Cart cartProduct: inCart) {
            ProductsStoreResponse toAdd = ProductsStoreResponse.builder()
                    .productId(cartProduct.getCartId().getProduct().getProductId())
                    .name(cartProduct.getCartId().getProduct().getName())
                    .description(cartProduct.getCartId().getProduct().getDescription())
                    .price(cartProduct.getCartId().getProduct().getPrice())
                    .quantity(cartProduct.getQuantity())
                    .build();
            products.add(toAdd);
            totalPrice += cartProduct.getQuantity() * cartProduct.getCartId().getProduct().getPrice();
        }

        if (user.getMoney() < totalPrice) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough money on account");

        Date dateOfPurchase = new Date();

        for (Cart cartProduct: inCart) {
            purchasesRepo.save(Purchases.builder()
                    .appUser(user)
                    .product(cartProduct.getCartId().getProduct())
                    .dateOfPurchase(dateOfPurchase)
                    .price(cartProduct.getCartId().getProduct().getPrice() * cartProduct.getQuantity())
                    .quantity(cartProduct.getQuantity())
                    .build());
        }

        user.setMoney(user.getMoney() - totalPrice);
        appUserRepo.save(user);

        return PurchaseResponse.builder()
                .dateOfPurchase(dateOfPurchase)
                .totalPrice(totalPrice)
                .products(products)
                .build();
    }
}
