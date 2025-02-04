package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shoppingcart.models.AppRole;

public interface JpaAppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByRoleName(String roleName);
}