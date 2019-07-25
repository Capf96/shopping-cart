package shoppingcart.rest;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import shoppingcart.requests.TrustRequest;
import shoppingcart.responses.TrustResponse;
import shoppingcart.restServices.TrustRequestsService;

import javax.validation.Valid;
import java.util.List;

@Api(description="Operations pertaining to trusting system in Shopping Cart API", tags = "Trust")
@RestController
@RequestMapping("/api/")
public class TrustController {
    @Autowired
    TrustRequestsService trustRequestsService;

    @ApiOperation(value = "View the trusted users from the requested user", response = TrustResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user trusted users"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/users/{truster}/trust/")
    public TrustResponse getTrustees(@PathVariable String truster) {
        return trustRequestsService.manageGet(truster);
    }

    @ApiOperation(value = "View your own trusted users", response = TrustResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved trusted users"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/trusted-users/")
    public TrustResponse getOwnTrustees() {
        return trustRequestsService.manageGetOwn();
    }

    @ApiOperation(value = "Add an user to your trusted users", response = TrustResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added user to trusted users"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 409, message = "The resource is already in database")
    })
    @PutMapping("/trusted-users/")
    public TrustResponse putTrust(@Valid @RequestBody TrustRequest trust) {
        return trustRequestsService.managePut(trust);
    }

    @ApiOperation(value = "Delete an user from your trusted users")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted user from trusted users"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/trusted-users/{trustee}/")
    public void deleteTrust(@PathVariable String trustee) {
        trustRequestsService.manageDelete(trustee);
    }
}
