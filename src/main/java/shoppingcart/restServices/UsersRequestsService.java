package shoppingcart.restServices;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import shoppingcart.models.AppUser;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.requests.AppUserRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.services.EncryptedPasswordsUtils;;

@Service
public class UsersRequestsService {

    @Autowired
    EncryptedPasswordsUtils encryptor;
    
    @Autowired
    JpaAppUserRepository appUserRepo;

    // GET METHODS

    public List<AppUserResponse> manageGet() {
        List<AppUser> allAppUsers = appUserRepo.findAll();
        List<AppUserResponse> responseList = new ArrayList<>();
        for (AppUser user: allAppUsers) {
            AppUserResponse toAdd = AppUserResponse.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername()).email(user.getEmail())
                    .firstName(user.getFirstName()).lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .enabled(user.getEnabled())
                    .money(user.getMoney())
                    .build();
            responseList.add(toAdd);
        }
        return responseList;
    }

    public AppUserResponse getMappingPathVariable(String username) {
        AppUser found = appUserRepo.findByUsername(username);
        if (found == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return AppUserResponse.builder()
                .userId(found.getUserId())
                .username(found.getUsername())
                .email(found.getEmail())
                .firstName(found.getFirstName())
                .lastName(found.getLastName())
                .phoneNumber(found.getPhoneNumber())
                .enabled(found.getEnabled())
                .money(found.getMoney()).build();
    }

    // POST

    public ResponseEntity<AppUserResponse> managePost(AppUserRequest user) {
        AppUser exists = appUserRepo.findByUsername(user.getUsername());
        if (exists != null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Username already in use");

        AppUser newUser = AppUser.builder().username(user.getUsername())
                            .encryptedPassword(encryptor.encryptPassword(user.getPassword())).email(user.getEmail())
                            .enabled(1).firstName(user.getFirstName()).lastName(user.getLastName())
                            .phoneNumber(user.getPhoneNumber()).money(user.getMoney()).build();
        AppUser inserted = appUserRepo.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AppUserResponse.builder().userId(inserted.getUserId()).username(inserted.getUsername())
                .email(inserted.getEmail()).firstName(inserted.getFirstName()).lastName(inserted.getLastName())
                .phoneNumber(inserted.getPhoneNumber()).money(inserted.getMoney()).build());
    }

    // DELETE

    public ResponseEntity<HttpStatus> manageDelete(String username) {
        AppUser toDelete = appUserRepo.findByUsername(username);
        if (toDelete == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        appUserRepo.delete(toDelete);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PATCH

    public AppUserResponse managePatch(String username, Map<Object, Object> values) {
        // TODO:
        //  - Create and user dto for patch
        AppUser toModify = appUserRepo.findByUsername(username);
        if (toModify == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        values.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(AppUser.class, (String) k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, toModify, v);
            }
        });
        appUserRepo.save(toModify);

        return AppUserResponse.builder().userId(toModify.getUserId()).username(toModify.getUsername())
                .email(toModify.getEmail()).firstName(toModify.getFirstName()).lastName(toModify.getLastName())
                .phoneNumber(toModify.getPhoneNumber()).money(toModify.getMoney()).build();
    }
}