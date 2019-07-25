package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shoppingcart.models.AppUser;
import shoppingcart.models.Trust;
import shoppingcart.models.TrustIdentity;

import java.util.List;

public interface JpaTrustRepository extends JpaRepository<Trust, TrustIdentity> {
    List<Trust> findByTrust_Truster_Username(String username);
    Trust findByTrust_Truster_UsernameAndTrust_Trustee_Username(String truster, String trustee);
    Trust findByTrust(TrustIdentity trust);
}
