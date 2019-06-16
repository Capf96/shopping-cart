package shoppingcart.repository;

import org.springframework.data.jpa.repository.Query;
import shoppingcart.models.AppUser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
    List<AppUser> findAll();
    @Query("select au from AppUser au, UserRole ur where ur.userRole.appUser.username = au.username and ur.userRole.appRole.roleName = ?1")
    List<AppUser> findByRoleName(String RoleName);
}