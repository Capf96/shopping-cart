package shoppingcart.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import shoppingcart.requests.RatingsRequest;
import shoppingcart.requests.RatingsPatchRequest;
import shoppingcart.responses.RatingsResponse;
import shoppingcart.restServices.RatingsRequestsService;

@Api(value="Ratings Management System", description="Operations pertaining to roles of users in Shopping Cart API")
@RestController
@RequestMapping("/api/users/{username}/ratings/")
public class RatingsController {
    @Autowired
    RatingsRequestsService ratingsRequestsService;

    // GET

    @ApiOperation(value = "View a list of ratings given to an specific user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping
    public List<RatingsResponse> getRatings(@PathVariable String username) {
        return ratingsRequestsService.manageGet(username);
    }

    // PUT

    @ApiOperation(value = "Add a rating to an user", response = RatingsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added rating"),
            @ApiResponse(code = 400, message = "The request was badly formed"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @PutMapping
    public RatingsResponse putRatings(@PathVariable String username, @Valid @RequestBody RatingsRequest rating) {
        return ratingsRequestsService.managePut(username, rating);
    }

    // PATCH

    @ApiOperation(value = "Update an specific rating given to an specific user", response = RatingsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated rating"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @PatchMapping("/{ratingId}/")
    public RatingsResponse patchRatings(@PathVariable String username, @PathVariable Long ratingId, @Valid @RequestBody RatingsPatchRequest patch) {
        return ratingsRequestsService.managePatch(username, ratingId, patch);
    }

    // DELETE

    @ApiOperation(value = "Delete an specific rating given to an specific user", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted rating"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @DeleteMapping("/{ratingId}/")
    public ResponseEntity <HttpStatus> deleteRatings(@PathVariable String username, @PathVariable Long ratingId) {
        return ratingsRequestsService.manageDelete(username, ratingId);
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