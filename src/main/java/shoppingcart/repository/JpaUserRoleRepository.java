package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shoppingcart.models.UserRole;

import java.util.List;

public interface JpaUserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("select ar.roleName from UserRole ur, AppRole ar where ur.appRole = ar.roleId and ar.roleId = ?1")
    List<String> getRoleNameByUserId(Long appUserId);
}