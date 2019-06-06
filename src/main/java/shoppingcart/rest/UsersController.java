package shoppingcart.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import shoppingcart.requests.AppUserRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.restServices.UsersRequestsService;

@Api(value="Users Management System", description="Operations pertaining to users in Shopping Cart API")
@RestController
@RequestMapping("/api/users/")
public class UsersController {
    @Autowired
    UsersRequestsService usersRequestsService;

    @ApiOperation(value = "View a list of available users", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping
    public List<AppUserResponse> getUsers() {
        return usersRequestsService.manageGet();
    }

    @ApiOperation(value = "View the requested user", response = AppUserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{username}/")
    public AppUserResponse getUserById(@PathVariable String username) {
        return usersRequestsService.getMappingPathVariable(username);
    }

    @ApiOperation(value = "Create a new user", response = AppUserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created user"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Creating the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 412, message = "Creating the resource is not possible as it fails database precondition")
    })
    @PostMapping
    public ResponseEntity <AppUserResponse> createUser(
        @ApiParam(value = "UserRequest Object to work with data in the database", required = true) @RequestBody @Valid AppUserRequest newUser) {
        return usersRequestsService.managePost(newUser);
    }

    @ApiOperation(value = "Delete an user", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted user"),
            @ApiResponse(code = 401, message = "You are not authorized to delete the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to delete is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @DeleteMapping("/{username}/")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String username) {
        return usersRequestsService.manageDelete(username);
    }

    @ApiOperation(value = "Change an user information", response = AppUserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to update is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PatchMapping("/{username}/")
    public AppUserResponse modify(@PathVariable String username, @RequestBody Map<Object, Object> values) {
        return usersRequestsService.managePatch(username, values);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		return ex.getBindingResult()
			.getAllErrors().stream()
			.map(ObjectError::getDefaultMessage)
			.collect(Collectors.toList());
	}
}