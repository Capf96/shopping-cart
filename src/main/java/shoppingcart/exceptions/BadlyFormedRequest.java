package shoppingcart.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class BadlyFormedRequest {
    private Date timestamp;
    private Integer status;
    private List<String> details;
}
