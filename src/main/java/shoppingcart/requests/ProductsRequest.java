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
public class ProductsRequest {
    @NotNull
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    private Boolean visible;

    @NotNull
    @Min(0)
    private Integer quantity;

}