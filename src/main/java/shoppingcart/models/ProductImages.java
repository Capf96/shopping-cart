package shoppingcart.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_images")
public class ProductImages {
    @Id
    @GeneratedValue
    @Column(name = "product_image_id", nullable = false, updatable = false)
    private Long productImageId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Products product;

    @Column(name = "path", nullable = false)
    private String path;
}
