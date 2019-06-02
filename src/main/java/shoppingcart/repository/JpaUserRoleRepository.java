package shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shoppingcart.models.AppRole;
import shoppingcart.models.UserRole;

import java.util.List;

public interface JpaUserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("select ar.roleName from UserRole ur, AppRole ar where ur.appRole = ar.roleId and ar.roleId = ?1")
    List<String> findRoleNameByUserId(Long appUserId);
    @Query("select ar from UserRole ur, AppRole ar, AppUser au where ur.appRole = ar.roleId and au.userId = ur.appUser and au.username = ?1")
    List<AppRole> findRolesByUsername(String username);
    @Query("select ur from UserRole ur, AppRole ar, AppUser au where au.username = ?1 and ar.roleName = ?2 and ur.appUser = au.userId and ur.appRole = ar.roleId")
    UserRole findByUsernameAndRoleName(String username, String roleName);
}