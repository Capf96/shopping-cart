package shoppingcart.rest;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.ReflectionUtils;
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

import shoppingcart.requests.AppUserRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.services.UsersRequestsService;

@RestController
@RequestMapping("/users/")
public class UsersController {
    @Autowired
    UsersRequestsService usersRequestsService;

    @GetMapping
    public List<AppUserResponse> getUsers() {
        return usersRequestsService.manageGet();
    }

    @GetMapping("/{username}/")
    public AppUserResponse getUserById(@PathVariable String username) {
        return usersRequestsService.getMappingPathVariable(username);
    }

    @PostMapping
    public AppUserResponse createUser(@RequestBody @Valid AppUserRequest newUser) {
        return usersRequestsService.managePost(newUser);
    }

    @DeleteMapping("/{username}/")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String username) {
        return usersRequestsService.manageDelete(username);
    }

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