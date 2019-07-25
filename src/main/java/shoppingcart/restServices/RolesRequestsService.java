package shoppingcart.restServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import shoppingcart.models.AppRole;
import shoppingcart.models.AppUser;
import shoppingcart.models.UserRole;
import shoppingcart.models.UserRoleIdentity;
import shoppingcart.repository.JpaAppRoleRepository;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaUserRoleRepository;
import shoppingcart.requests.AppRoleRequest;
import shoppingcart.responses.AppRoleResponse;
import shoppingcart.responses.UserRoleResponse;

@Service
public class RolesRequestsService {
    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaAppRoleRepository appRoleRepo;

    @Autowired
    JpaUserRoleRepository userRoleRepo;

    // GET

    public UserRoleResponse manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<UserRole> userRoles = userRoleRepo.findByUserRole_AppUser_Username(username);
        List<String> roles = new ArrayList<>();
        for (UserRole role: userRoles) {

            roles.add(role.getUserRole().getAppRole().getRoleName());
        }
        return UserRoleResponse.builder()
                .username(username)
                .roles(roles)
                .build();
    }

    // PUT

    public UserRoleResponse managePut(String username, AppRoleRequest role) {
        AppRole appRole = appRoleRepo.findByRoleName(role.getRoleName());
        if (appRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        UserRole exists = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(username, role.getRoleName());
        if (exists != null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "The user already has that role");

        UserRole toInsert = UserRole.builder()
                .userRole(UserRoleIdentity.builder()
                    .appUser(appUser)
                    .appRole(appRole)
                    .build())
                .build();
        UserRole inserted = userRoleRepo.save(toInsert);

        List<UserRole> roles = userRoleRepo.findByUserRole_AppUser_Username(username);

        List<String> roleNames = new ArrayList<>();
        for (UserRole userRole: roles) {
            roleNames.add(userRole.getUserRole().getAppRole().getRoleName());
        }

        return UserRoleResponse.builder()
                .username(inserted.getUserRole().getAppUser().getUsername())
                .roles(roleNames)
                .build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> manageDelete(String username, String roleName) {
        AppRole appRole = appRoleRepo.findByRoleName(roleName);
        if (appRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        UserRole userRole = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(username, roleName);
        if (userRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That user doesn't have that role");
        userRoleRepo.delete(userRole);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}