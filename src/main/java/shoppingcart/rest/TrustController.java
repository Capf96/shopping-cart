package shoppingcart.rest;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import shoppingcart.requests.TrustRequest;
import shoppingcart.responses.TrustResponse;
import shoppingcart.restServices.TrustRequestsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class TrustController {
    @Autowired
    TrustRequestsService trustRequestsService;

    @GetMapping("/users/{truster}/trust/")
    public List<TrustResponse> getTrustees(@PathVariable String truster) {
        return trustRequestsService.manageGet(truster);
    }

    @GetMapping("/trusted-users/")
    public List<TrustResponse> getOwnTrustees() {
        return trustRequestsService.manageGetOwn();
    }

    @PutMapping("/trusted-users/")
    public TrustResponse putTrust(@Valid @RequestBody TrustRequest trust) {
        return trustRequestsService.managePut(trust);
    }

    @DeleteMapping("/trusted-users/{trustee}/")
    public ResponseEntity <HttpStatus> deleteTrust(@PathVariable String trustee) {
        return trustRequestsService.manageDelete(trustee);
    }
}
