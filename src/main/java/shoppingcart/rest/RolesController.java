package shoppingcart.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoppingcart.models.AppRole;
import shoppingcart.models.AppUser;
import shoppingcart.models.UserRole;
import shoppingcart.repository.JpaAppRoleRepository;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaUserRoleRepository;

@RestController
@RequestMapping("/users/{username}/roles/")
public class RolesController {
    @Autowired
    JpaUserRoleRepository userRoleRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaAppRoleRepository appRoleRepo;

    @GetMapping
    public List<AppRole> getRoles(@PathVariable String username) {
        return userRoleRepo.getRolesByUsername(username);
    }

    @PutMapping
    public UserRole addRoleToUser(@PathVariable String username, @RequestBody AppRole role) {
        AppUser user = appUserRepo.getByUsername(username);
        UserRole userRole = new UserRole();
        userRole.setAppUser(user);
        userRole.setAppRole(role);
        userRoleRepo.save(userRole);
        return userRole;
    }

    @DeleteMapping("/{roleName}/")
    public UserRole removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        UserRole userRole = userRoleRepo.getByUsernameAndRoleName(username, roleName);
        userRoleRepo.delete(userRole);
        return userRole;
    }
}