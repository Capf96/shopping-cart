package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingcart.models.AppUser;
import shoppingcart.models.Products;

import java.util.List;

public interface JpaProductsRepository extends JpaRepository<Products, Long> {
    public Products findByProductId(Long productId);
    public List<Products> findBySeller(AppUser seller);
}
