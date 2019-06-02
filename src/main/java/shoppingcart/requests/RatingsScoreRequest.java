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
@NoArgsConstructor
@AllArgsConstructor
public class RatingsScoreRequest {
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    private String dummy;
}