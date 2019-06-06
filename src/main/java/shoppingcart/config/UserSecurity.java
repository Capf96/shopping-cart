package shoppingcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import shoppingcart.models.Ratings;
import shoppingcart.repository.JpaRatingsRepository;

@Configuration
public class UserSecurity {
    @Autowired
    JpaRatingsRepository ratingsRepo;

    public boolean hasUsername(Authentication authentication, String username) {
        if (authentication.getPrincipal() == "anonymousUser") return false;
        User loggedUser = (User) authentication.getPrincipal();
        return loggedUser.getUsername().equals(username);
    }

    public boolean ratingGivenByUser(Authentication authentication, String username, Long ratingId) {
        if (authentication.getPrincipal() == "anonymousUser") return false;
        User loggedUser = (User) authentication.getPrincipal();

        Ratings rating = ratingsRepo.findByRatingId(ratingId);
        return !(rating == null || !rating.getRater().getUsername().equals(loggedUser.getUsername()));
    }
}