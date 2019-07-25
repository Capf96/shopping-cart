package shoppingcart.rest;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoppingcart.requests.AppRoleRequest;
import shoppingcart.responses.AppRoleResponse;
import shoppingcart.responses.UserRoleResponse;
import shoppingcart.restServices.RolesRequestsService;

@Api(description="Operations pertaining to roles of users in Shopping Cart API", tags = "Roles")
@RestController
@RequestMapping("/api/users/{username}/roles/")
public class RolesController {
    @Autowired
    RolesRequestsService rolesRequestsService;

    @ApiOperation(value = "View a list of available roles of an specific user", response = UserRoleResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping
    public UserRoleResponse getRoles(@PathVariable String username) {
        return rolesRequestsService.manageGet(username);
    }

    @ApiOperation(value = "Add a role to an specific user", response = UserRoleResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added role to user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found or the role was not found"),
            @ApiResponse(code = 412, message = "The user already has that role")
    })
    @PutMapping
    public UserRoleResponse addRoleToUser(@PathVariable String username, @RequestBody @Valid AppRoleRequest role) {
        return rolesRequestsService.managePut(username, role);
    }

    @ApiOperation(value = "Delete a role of an specific user")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted role from user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found or the role was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{roleName}/")
    public void removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        rolesRequestsService.manageDelete(username, roleName);
    }
}