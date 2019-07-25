package shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import shoppingcart.requests.TrustRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.responses.TrustResponse;
import shoppingcart.services.JSessionId;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping
public class TrustFrontController {
    @Autowired
    JSessionId jSessionId;

    final private String trustApi = "http://localhost:8080/api/trusted-users/";
    final private String usersApi = "http://localhost:8080/api/users/";

    @GetMapping("/trusted-users")
    public String trustedUsersPage(Model model) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<TrustResponse> response = restTemplate.exchange(trustApi, HttpMethod.GET, entity, TrustResponse.class);

        TrustResponse trust = response.getBody();

        model.addAttribute("trust", trust);

        return "trustedUsersPage";
    }

    @PostMapping(value = "/trusted-users", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addTrust(TrustRequest newTrust) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        Map<String, String> request = new HashMap<String, String>(){
            {
                put("trustee", newTrust.getTrustee());
            }
        };
        HttpEntity entity = new HttpEntity(request, headers);

        try {
            HttpEntity<TrustResponse> response = restTemplate.exchange(trustApi, HttpMethod.PUT, entity, TrustResponse.class);
        } catch (HttpClientErrorException ex) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, String> map = mapper.readValue(ex.getResponseBodyAsString(), Map.class);
                return "redirect:/trusted-users?error=" + map.get("message");
            } catch (IOException io) {
                return "redirect:/trusted-users";
            }
        }
        return "redirect:/trusted-users";
    }

    @PostMapping("/trusted-users/{trustee}/delete")
    public String deleteTrust(@PathVariable String trustee) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<TrustResponse> response = restTemplate.exchange(trustApi + trustee + "/", HttpMethod.DELETE, entity, TrustResponse.class);

        return "redirect:/trusted-users";
    }

    @GetMapping("/trustSystem")
    public String searchTrust() {
        return "trustsPage";
    }

    @PostMapping("/trustSystem")
    public String displayTrust(@RequestParam("truster") String truster, Model model) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<TrustResponse> response = restTemplate.exchange(usersApi + truster + "/trust/", HttpMethod.GET, entity, TrustResponse.class);

        TrustResponse trust = response.getBody();

        System.out.println(trust);

        model.addAttribute("trust", trust);

        return "trustsPage";
    }
}
