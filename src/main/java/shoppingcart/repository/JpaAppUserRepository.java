package shoppingcart.repository;

import org.springframework.data.jpa.repository.Query;
import shoppingcart.models.AppUser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findAll();
    AppUser findByUsername(String username);
    @Query("select au from AppUser au, UserRole ur where ur.appUser = au.userId and ur.appRole.roleName = ?1")
    List<AppUser> findByRoleName(String RoleName);
}