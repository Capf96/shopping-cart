package shoppingcart.rest;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoppingcart.requests.RatingsRequest;
import shoppingcart.requests.RatingsScoreRequest;
import shoppingcart.responses.RatingsResponse;
import shoppingcart.services.RatingsRequestsService;

@RestController
@RequestMapping("/users/{username}/ratings/")
public class RatingsController {
    @Autowired
    RatingsRequestsService ratingsRequestsService;

    // GET

    @GetMapping
    public List<RatingsResponse> getRatings(@PathVariable String username) {
        return ratingsRequestsService.manageGet(username);
    }

    // PUT

    @PutMapping
    public RatingsResponse putRatings(@PathVariable String username, @Valid @RequestBody RatingsRequest rating) {
        return ratingsRequestsService.managePut(username, rating);
    }

    // PATCH

    @PatchMapping("/{ratingId}/")
    public RatingsResponse patchRatings(@PathVariable String username, @PathVariable Long ratingId, @RequestBody RatingsScoreRequest patch) {
        return ratingsRequestsService.managePatch(username, ratingId, patch);
    }

    // DELETE

    @DeleteMapping("/{ratingId}/")
    public ResponseEntity <HttpStatus> deleteRatings(@PathVariable String username, @PathVariable Long ratingId) {
        return ratingsRequestsService.manageDelete(username, ratingId);
    }

}