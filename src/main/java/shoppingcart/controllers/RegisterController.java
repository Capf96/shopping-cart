package shoppingcart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import shoppingcart.requests.AppUserRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.services.JSessionId;

@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    JSessionId jSessionId;

    private final String usersApi = "http://localhost:8080/api/users/";

    @GetMapping
    public String takeInformation() {
        return "registrationPage";
    }

    @PostMapping
    public String registerUser(AppUserRequest userInfo) {
        userInfo.setMoney(0.0);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity entity = new HttpEntity(userInfo, jSessionId.getJSessionId());

        HttpEntity<AppUserResponse> response = restTemplate.exchange(usersApi, HttpMethod.POST, entity, AppUserResponse.class);

        System.out.println(response.getBody());

        return "redirect:/login";
    }
}
