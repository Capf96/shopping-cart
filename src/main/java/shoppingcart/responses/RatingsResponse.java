package shoppingcart.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingsResponse {
    private Long ratingId;
    private String rater;
    private String rated;
    private Integer rating;
}