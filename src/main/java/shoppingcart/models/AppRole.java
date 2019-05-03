package shoppingcart.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "app_role",
        uniqueConstraints = {
        @UniqueConstraint(name = "APP_ROLE_UK", columnNames = "Role_Name") })
public class AppRole {
    @Id
    @GeneratedValue
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_name", length = 30, nullable = false)
    private String roleName;
}
