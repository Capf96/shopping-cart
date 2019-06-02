package shoppingcart.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoppingcart.requests.AppRoleRequest;
import shoppingcart.responses.AppRoleResponse;
import shoppingcart.responses.UserRoleResponse;
import shoppingcart.services.RolesRequestsService;

@RestController
@RequestMapping("/users/{username}/roles/")
public class RolesController {
    @Autowired
    RolesRequestsService rolesRequestsService;

    @GetMapping
    public List<AppRoleResponse> getRoles(@PathVariable String username) {
        return rolesRequestsService.manageGet(username);
    }

    @PutMapping
    public UserRoleResponse addRoleToUser(@PathVariable String username, @RequestBody @Valid AppRoleRequest role) {
        return rolesRequestsService.managePut(username, role);
    }

    @DeleteMapping("/{roleName}/")
    public ResponseEntity <HttpStatus> removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        return rolesRequestsService.manageDelete(username, roleName);
    }
}