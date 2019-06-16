package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingcart.models.Purchases;

public interface JpaPurchasesRepository extends JpaRepository<Purchases, Long> {
}
