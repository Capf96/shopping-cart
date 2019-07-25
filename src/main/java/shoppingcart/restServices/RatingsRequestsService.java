package shoppingcart.restServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import shoppingcart.models.AppUser;
import shoppingcart.models.Ratings;
import shoppingcart.models.UserRole;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaRatingsRepository;
import shoppingcart.repository.JpaUserRoleRepository;
import shoppingcart.requests.RatingsRequest;
import shoppingcart.requests.RatingsPatchRequest;
import shoppingcart.responses.RatingResponse;
import shoppingcart.responses.RatingsResponse;
import shoppingcart.responses.SingleRatingResponse;

@Service
public class RatingsRequestsService {
    @Autowired
    JpaRatingsRepository ratingsRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaUserRoleRepository userRoleRepo;

    // GET

    public RatingsResponse manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Ratings> ratings = ratingsRepo.findByRatedUsername(username);
        List<RatingResponse> userRatings = new ArrayList<>();
        for (Ratings rating: ratings) {
            RatingResponse toAdd = RatingResponse.builder()
                                        .ratingId(rating.getRatingId())
                                        .rater(rating.getRater().getUsername())
                                        .rating(rating.getRating()).build();
            userRatings.add(toAdd);
        }
        return RatingsResponse.builder()
                .username(username)
                .ratings(userRatings)
                .build();
    }

    // PUT

    public SingleRatingResponse managePut(String username, RatingsRequest rating) {
        // TODO:
        //  - Fix the response when the request is badly formed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        AppUser rater = appUserRepo.findByUsername(user.getUsername());

        AppUser rated = appUserRepo.findByUsername(username);
        if (rated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rated user not found");

        UserRole isSeller = userRoleRepo
                .findByUserRole_AppUser_UsernameAndUserRole_AppRole_RoleName(rated.getUsername(), "ROLE_SELLER");
        if (isSeller == null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Rated user is not a seller");

        if (user.getUsername().equals(rated.getUsername())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must not be same user");

        Ratings newRating = Ratings.builder()
                .rater(rater)
                .rated(rated)
                .rating(rating.getRating())
                .build();

        Ratings saved = ratingsRepo.save(newRating);

        return SingleRatingResponse.builder()
                .ratingId(saved.getRatingId())
                .rater(saved.getRater().getUsername())
                .rated(saved.getRated().getUsername())
                .rating(saved.getRating()).build();
    }

    // PATCH

    public SingleRatingResponse managePatch(Long ratingId, RatingsPatchRequest patch) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Ratings rating = ratingsRepo.findByRatingId(ratingId);
        if (rating == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");

        if (!user.getUsername().equals(rating.getRater().getUsername())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not resource owner");

        rating.setRating(patch.getRating());

        Ratings saved = ratingsRepo.save(rating);

        return SingleRatingResponse.builder()
                .ratingId(saved.getRatingId())
                .rater(saved.getRater().getUsername())
                .rated(saved.getRated().getUsername())
                .rating(saved.getRating())
                .build();
    }

    // DELETE

    public void manageDelete(Long ratingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Ratings rating = ratingsRepo.findByRatingId(ratingId);
        if (rating == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");

        if (!user.getUsername().equals(rating.getRater().getUsername())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not resource owner");

        ratingsRepo.delete(rating);
    }
}