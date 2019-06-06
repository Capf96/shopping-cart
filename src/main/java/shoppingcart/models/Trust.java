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
@Table(name = "trust")
public class Trust {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "trust_id", nullable = false)
    private Long trustId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "truster", nullable = false)
    private AppUser truster;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "trustee", nullable = false)
    private AppUser trustee;
}
