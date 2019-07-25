package shoppingcart.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleRatingResponse {
    private Long ratingId;
    private String rater;
    private String rated;
    private Integer rating;
}
