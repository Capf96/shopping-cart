package shoppingcart.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import shoppingcart.models.AppUser;
import shoppingcart.models.Ratings;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaRatingsRepository;
import shoppingcart.requests.RatingsRequest;
import shoppingcart.requests.RatingsScoreRequest;
import shoppingcart.responses.RatingsResponse;

@Service
public class RatingsRequestsService {
    @Autowired
    JpaRatingsRepository ratingsRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    public List<RatingsResponse> manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Ratings> ratings = ratingsRepo.findByRatedUsername(username);
        List<RatingsResponse> responseList = new ArrayList<>();
        for (Ratings rating: ratings) {
            RatingsResponse toAdd = RatingsResponse.builder()
                                        .ratingId(rating.getRatingId())
                                        .rater(rating.getRater().getUsername())
                                        .rated(rating.getRated().getUsername())
                                        .rating(rating.getRating()).build();
            responseList.add(toAdd);
        }
        return responseList;
    }

    public RatingsResponse managePut(String username, RatingsRequest rating) {
        AppUser rated = appUserRepo.findByUsername(username);
        if (rated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rated user not found");

        AppUser rater = appUserRepo.findByUsername(rating.getRater());
        if (rater == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rater user not found");

        Ratings newRating = Ratings.builder().rater(rater)
                                .rated(rated).rating(rating.getRating()).build();

        Ratings saved = ratingsRepo.save(newRating);

        return RatingsResponse.builder().ratingId(saved.getRatingId())
                    .rater(saved.getRater().getUsername())
                    .rated(saved.getRated().getUsername())
                    .rating(saved.getRating()).build();
    }

    public RatingsResponse managePatch(String username, Long ratingId, RatingsScoreRequest patch) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Ratings rating = ratingsRepo.findByRatingId(ratingId);
        if (rating == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");

        rating.setRating(patch.getRating());

        Ratings saved = ratingsRepo.save(rating);

        return RatingsResponse.builder().ratingId(saved.getRatingId())
                    .rater(saved.getRater().getUsername())
                    .rated(saved.getRated().getUsername())
                    .rating(saved.getRating()).build();
    }

    public ResponseEntity <HttpStatus> manageDelete(String username, Long ratingId) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Ratings rating = ratingsRepo.findByRatingId(ratingId);
        if (rating == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found");

        ratingsRepo.delete(rating);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}