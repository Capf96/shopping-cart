package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shoppingcart.models.AppUser;
import shoppingcart.models.Trust;

import java.util.List;

public interface JpaTrustRepository extends JpaRepository<Trust, Long> {
    public List<Trust> findByTrusterUsername(String username);
//    @Query("select t from Trust t, AppUser tr, AppUser td where t.truster = tr.appUserId and " +
//            "t.trusted = td.appUserId and tr.appUserId = ?1 and td.appUserId = ?2")
    public Trust findByTrusterAndTrustee(AppUser truster, AppUser trustee);
}
