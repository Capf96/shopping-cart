package shoppingcart.services;

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

    public List<AppRoleResponse> manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<AppRole> userRoles = userRoleRepo.findRolesByUsername(username);
        List<AppRoleResponse> responseList = new ArrayList<>();
        for (AppRole role: userRoles) {
            AppRoleResponse toAdd = AppRoleResponse.builder().roleId(role.getRoleId())
                                        .roleName(role.getRoleName()).build();
            responseList.add(toAdd);
        }
        return responseList;
    }

    // PUT

    public UserRoleResponse managePut(String username, AppRoleRequest role) {
        AppRole appRole = appRoleRepo.findByRoleName(role.getRoleName());
        if (appRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        UserRole exists = userRoleRepo.findByUsernameAndRoleName(username, role.getRoleName());
        if (exists != null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "The user already has that role");

        UserRole toInsert = UserRole.builder().appRole(appRole).appUser(appUser).build();
        UserRole inserted = userRoleRepo.save(toInsert);

        return UserRoleResponse.builder().userRoleId(inserted.getUserRoleId())
                    .appUserId(inserted.getAppUser().getUserId())
                    .appRoleId(inserted.getAppRole().getRoleId()).build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> manageDelete(String username, String roleName) {
        AppRole appRole = appRoleRepo.findByRoleName(roleName);
        if (appRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");

        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        UserRole userRole = userRoleRepo.findByUsernameAndRoleName(username, roleName);
        if (userRole == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "That user doesn't have that role");
        userRoleRepo.delete(userRole);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}