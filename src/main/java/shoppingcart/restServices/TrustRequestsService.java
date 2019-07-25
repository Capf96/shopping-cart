package shoppingcart.restServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shoppingcart.models.AppUser;
import shoppingcart.models.Trust;
import shoppingcart.models.TrustIdentity;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaTrustRepository;
import shoppingcart.requests.TrustRequest;
import shoppingcart.responses.TrustResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrustRequestsService {
    @Autowired
    JpaTrustRepository trustRepo;

    @Autowired
    JpaAppUserRepository appUserRepo;

    // GET

    public TrustResponse manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Trust> trustedList = trustRepo.findByTrust_Truster_Username(username);
        List<String> trustedUsers = new ArrayList<>();
        for (Trust trust: trustedList) {
            trustedUsers.add(trust.getTrust().getTrustee().getUsername());
        }

        return TrustResponse.builder()
                .username(user.getUsername())
                .trustedUsers(trustedUsers)
                .build();
    }

    public TrustResponse manageGetOwn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return manageGet(user.getUsername());
    }

    // PUT

    public TrustResponse managePut(TrustRequest trustRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        AppUser truster = appUserRepo.findByUsername(user.getUsername());
        if (truster == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        AppUser trustee = appUserRepo.findByUsername(trustRequest.getTrustee());
        if (trustee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        if (truster.getUsername().equals(trustee.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't trust yourself");

        TrustIdentity trustIdentity = TrustIdentity.builder().truster(truster).trustee(trustee).build();

        Trust exists = trustRepo.findByTrust(trustIdentity);
        if (exists != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Trust already in database");

        Trust newTrust = Trust.builder()
                .trust(trustIdentity)
                .build();

        Trust saved = trustRepo.save(newTrust);

        List<Trust> trusted = trustRepo.findByTrust_Truster_Username(user.getUsername());
        List<String> trustedUsers = new ArrayList<>();
        for (Trust trust: trusted) {
            trustedUsers.add(trust.getTrust().getTrustee().getUsername());
        }

        return TrustResponse.builder()
                .username(user.getUsername())
                .trustedUsers(trustedUsers)
                .build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> manageDelete(String trustedUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        AppUser truster = appUserRepo.findByUsername(user.getUsername());
        if (truster == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        AppUser trustee = appUserRepo.findByUsername(trustedUsername);
        if (trustee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Trust toDelete = trustRepo.findByTrust(TrustIdentity.builder().truster(truster).trustee(trustee).build());
        if (toDelete == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trust not found");

        trustRepo.delete(toDelete);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
