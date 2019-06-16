package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shoppingcart.models.AppUser;
import shoppingcart.models.Trust;
import shoppingcart.models.TrustIdentity;

import java.util.List;

public interface JpaTrustRepository extends JpaRepository<Trust, TrustIdentity> {
    public List<Trust> findByTrust_Truster_Username(String username);
    public Trust findByTrust(TrustIdentity trust);
}
