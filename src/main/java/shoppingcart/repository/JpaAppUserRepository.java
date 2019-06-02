package shoppingcart.repository;

import shoppingcart.models.AppUser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findAll();
    AppUser findByUsername(String username);
}