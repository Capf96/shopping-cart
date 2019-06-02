package shoppingcart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@Configuration
public class UserSecurity {
    public boolean hasUsername(Authentication authentication, String username) {
        User loggedUser = (User) authentication.getPrincipal();
        return loggedUser.getUsername().equals(username);
    }
}