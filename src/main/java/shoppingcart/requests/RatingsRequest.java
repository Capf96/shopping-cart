package shoppingcart.requests;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingsRequest {
    @NotNull
    private String rater;
    @NotNull
    @Max(value = 5)
    @Min(value = 1)
    private Integer rating;
}