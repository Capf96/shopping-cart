package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingcart.models.ProductImages;

import java.util.List;

public interface JpaProductImagesRepository extends JpaRepository<ProductImages, Long> {
    List<ProductImages> findByProduct_ProductId(Long productId);
    ProductImages findByProductImageId(Long productImageId);
}
