package shoppingcart.rest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoppingcart.models.AppUser;
import shoppingcart.repository.JpaAppUserRepository;

@RestController
@RequestMapping("/users/")
public class UsersController {
    @Autowired
    JpaAppUserRepository appUserRepo;

    @GetMapping
    public List<AppUser> getUsers() {
        return appUserRepo.findAll();
    }

    @GetMapping("/{username}/")
    public AppUser getUserById(@PathVariable String username) {
        return appUserRepo.getByUsername(username);
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser newUser) {
        appUserRepo.save(newUser);
        return newUser;
    }

    @DeleteMapping("/{username}/")
    public AppUser deleteUser(@PathVariable String username) {
        AppUser toDelete = appUserRepo.getByUsername(username);
        appUserRepo.delete(toDelete);
        return toDelete;
    }

    @PatchMapping("/{username}/")
    public AppUser modify(@PathVariable String username, @RequestBody Map<Object, Object> values) {
        AppUser toModify = appUserRepo.getByUsername(username);
        values.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(AppUser.class, (String) k);
            field.setAccessible(true);
            ReflectionUtils.setField(field, toModify, v);
        });
        appUserRepo.save(toModify);
        return toModify;
    }
}