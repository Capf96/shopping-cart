package shoppingcart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shoppingcart.models.AppRole;
import shoppingcart.models.AppUser;
import shoppingcart.models.UserRole;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaUserRoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private JpaAppUserRepository appUserRepo;

    @Autowired
    private JpaUserRoleRepository userRoleRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser appUser = this.appUserRepo.findByUsername(userName);

        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }

        System.out.println("Found User: " + appUser);

        List<UserRole> roleNames = this.userRoleRepo.findByUserRole_AppUser_Username(appUser.getUsername());

        System.out.println("Llega aqui");

        List<GrantedAuthority> grantList = new ArrayList<>();
        if (roleNames != null) {
            System.out.println(roleNames);
            for (UserRole role : roleNames) {
                GrantedAuthority authority = new SimpleGrantedAuthority(role.getUserRole().getAppRole().getRoleName());
                grantList.add(authority);
            }
        }

        return new User(appUser.getUsername(), appUser.getEncryptedPassword(), grantList);
    }
}
