package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingcart.models.AppUser;
import shoppingcart.models.Cart;
import shoppingcart.models.CartIdentity;
import shoppingcart.models.Products;

import java.util.List;

public interface JpaCartRepository extends JpaRepository<Cart, CartIdentity> {
    List<Cart> findByCartId_User(AppUser user);
    Cart findByCartId(CartIdentity cartIdentity);
}
