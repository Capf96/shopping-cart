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

    public List<TrustResponse> manageGet(String username) {
        AppUser user = appUserRepo.findByUsername(username);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        List<Trust> trustedList = trustRepo.findByTrusterUsername(username);
        List<TrustResponse> responseList = new ArrayList<>();
        for (Trust trust: trustedList) {
            TrustResponse toAdd = TrustResponse.builder()
                    .trustId(trust.getTrustId())
                    .truster(trust.getTruster().getUsername())
                    .trustee(trust.getTrustee().getUsername())
                    .build();
            responseList.add(toAdd);
        }

        return responseList;
    }

    public List<TrustResponse> manageGetOwn() {
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

        Trust exists = trustRepo.findByTrusterAndTrustee(truster, trustee);
        if (exists != null) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Trust already in database");

        Trust newTrust = Trust.builder()
                .truster(truster)
                .trustee(trustee)
                .build();

        Trust saved = trustRepo.save(newTrust);

        return TrustResponse.builder()
                .trustId(saved.getTrustId())
                .truster(saved.getTruster().getUsername())
                .trustee(saved.getTrustee().getUsername())
                .build();
    }

    // DELETE

    public ResponseEntity <HttpStatus> manageDelete(String trustedUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        AppUser truster = appUserRepo.findByUsername(user.getUsername());
        if (truster == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        AppUser trusted = appUserRepo.findByUsername(trustedUsername);
        if (trusted == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Trust toDelete = trustRepo.findByTrusterAndTrustee(truster, trusted);

        trustRepo.delete(toDelete);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
