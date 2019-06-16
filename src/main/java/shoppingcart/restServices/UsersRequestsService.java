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
import shoppingcart.requests.AppUserPatchRequest;
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
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
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
                AppUserResponse.builder().username(inserted.getUsername())
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

    public AppUserResponse managePatch(String username, AppUserPatchRequest patch) {
        AppUser toModify = appUserRepo.findByUsername(username);
        if (toModify == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        AppUser modified = AppUser.builder()
                .username(toModify.getUsername())
                .encryptedPassword(toModify.getEncryptedPassword())
                .email((patch.getEmail() != null)? patch.getEmail(): toModify.getEmail())
                .firstName((patch.getFirstName() != null)? patch.getFirstName(): toModify.getFirstName())
                .lastName((patch.getLastName() != null)? patch.getLastName(): toModify.getLastName())
                .phoneNumber((patch.getPhoneNumber() != null)? patch.getPhoneNumber(): toModify.getPhoneNumber())
                .money((patch.getMoney() != null)? patch.getMoney(): toModify.getMoney())
                .build();
        AppUser saved = appUserRepo.save(modified);

        return AppUserResponse.builder()
                .username(saved.getUsername())
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .phoneNumber(saved.getPhoneNumber())
                .money(saved.getMoney())
                .build();
    }
}