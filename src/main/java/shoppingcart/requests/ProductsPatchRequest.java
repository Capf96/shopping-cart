package shoppingcart.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingcart.models.AppUser;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsPatchRequest {
    private String name;

    private String description;

    @Min(0)
    private Double price;

    private Boolean visible;

    @Min(0)
    private Integer quantity;

}