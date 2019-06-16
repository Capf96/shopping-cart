package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shoppingcart.models.AppRole;
import shoppingcart.models.UserRole;
import shoppingcart.models.UserRoleIdentity;

import java.util.List;

public interface JpaUserRoleRepository extends JpaRepository<UserRole, UserRoleIdentity> {
    List<UserRole> findByUserRole_AppUser_Username(String username);
    UserRole findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(String username, String roleName);
}