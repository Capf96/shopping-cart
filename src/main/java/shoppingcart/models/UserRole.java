package shoppingcart.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole appRole;
}
