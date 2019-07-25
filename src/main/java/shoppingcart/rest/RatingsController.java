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
import shoppingcart.responses.SingleRatingResponse;
import shoppingcart.restServices.RatingsRequestsService;

@Api(description="Operations pertaining to roles of users in Shopping Cart API", tags = "Ratings")
@RestController
@RequestMapping("/api/")
public class RatingsController {
    @Autowired
    RatingsRequestsService ratingsRequestsService;

    // GET

    @ApiOperation(value = "View a list of ratings given to an specific user", response = RatingsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found")
    })
    @GetMapping("/users/{username}/ratings/")
    public RatingsResponse getRatings(@PathVariable String username) {
        return ratingsRequestsService.manageGet(username);
    }

    // PUT

    @ApiOperation(value = "Add a rating to an user", response = SingleRatingResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added rating"),
            @ApiResponse(code = 400, message = "The request was badly formed"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 412, message = "The user you were trying to rate is not a seller")
    })
    @PutMapping("/users/{username}/ratings/")
    public SingleRatingResponse putRatings(@PathVariable String username, @Valid @RequestBody RatingsRequest rating) {
        return ratingsRequestsService.managePut(username, rating);
    }

    // PATCH

    @ApiOperation(value = "Update a rating given to an user", response = SingleRatingResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated rating"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 412, message = "Not resource owner or rating yourself")
    })
    @PatchMapping("/ratings/{ratingId}/")
    public SingleRatingResponse patchRatings(@PathVariable Long ratingId, @Valid @RequestBody RatingsPatchRequest patch) {
        return ratingsRequestsService.managePatch(ratingId, patch);
    }

    // DELETE

    @ApiOperation(value = "Delete a rating given to an user")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted rating"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach was not found"),
            @ApiResponse(code = 412, message = "Not resource owner")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/ratings/{ratingId}/")
    public void deleteRatings(@PathVariable Long ratingId) {
        ratingsRequestsService.manageDelete(ratingId);
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