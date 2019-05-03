package shoppingcart.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "app_user",
        uniqueConstraints = {
        @UniqueConstraint(name = "APP_USER_UK", columnNames = "Username") })
public class AppUser {
    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "encrypted_password", length = 128, nullable = false)
    private String encryptedPassword;

    @Column(name = "enabled", length = 1, nullable = false)
    private boolean enabled;
}
