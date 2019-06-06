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
import shoppingcart.restServices.RolesRequestsService;

@Api(value="Roles Management System", description="Operations pertaining to roles of users in Shopping Cart API")
@RestController
@RequestMapping("/api/users/{username}/roles/")
public class RolesController {
    @Autowired
    RolesRequestsService rolesRequestsService;

    @ApiOperation(value = "View a list of available roles of an specific user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping
    public List<AppRoleResponse> getRoles(@PathVariable String username) {
        return rolesRequestsService.manageGet(username);
    }

    @ApiOperation(value = "Add a role to an specific user", response = UserRoleResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added role to user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found or the role was not found")
    })
    @PutMapping
    public UserRoleResponse addRoleToUser(@PathVariable String username, @RequestBody @Valid AppRoleRequest role) {
        return rolesRequestsService.managePut(username, role);
    }

    @ApiOperation(value = "Delete a role of an specific user", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted role from user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @DeleteMapping("/{roleName}/")
    public ResponseEntity <HttpStatus> removeRoleFromUser(@PathVariable String username, @PathVariable String roleName) {
        return rolesRequestsService.manageDelete(username, roleName);
    }
}