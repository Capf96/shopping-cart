package shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shoppingcart.models.Ratings;

public interface JpaRatingsRepository extends JpaRepository<Ratings, Long> {
    @Query("select r from Ratings r, AppUser au where r.rated = au.userId and au.username = ?1")
    public List<Ratings> findByRatedUsername(String username);
    public Ratings findByRatingId(Long ratingId);
}