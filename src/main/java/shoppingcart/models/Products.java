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
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "seller", nullable = false)
    private AppUser seller;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "visibility", nullable = false)
    private Boolean visible;

    @Column(name = "quantity")
    private Integer quantity;

}
