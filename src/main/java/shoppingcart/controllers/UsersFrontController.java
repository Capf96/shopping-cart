package shoppingcart.controllers;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import shoppingcart.dataHandlers.UserRolesHandler;
import shoppingcart.requests.AppUserPatchRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.responses.RatingsResponse;
import shoppingcart.responses.UserRoleResponse;
import shoppingcart.services.JSessionId;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping
public class UsersFrontController {
    @Autowired
    JSessionId jSessionId;

    final private String usersApi = "http://localhost:8080/api/users/";

    @GetMapping("/userInfo")
    public String userInfo(Model model, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<AppUserResponse> response = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/", HttpMethod.GET, entity, AppUserResponse.class);

        if (response.hasBody()) {
            AppUserResponse user = response.getBody();
            model.addAttribute("user", user);
        }

        HttpEntity<UserRoleResponse> rolesResponse = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/roles/", HttpMethod.GET, entity, UserRoleResponse.class);

        if (rolesResponse.hasBody()) {
            UserRoleResponse userRoles = rolesResponse.getBody();
            model.addAttribute("roles", userRoles);
        }

        return "userInfoPage";
    }

    @PostMapping(value = "/userInfo/updateRoles", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateRoles(UserRolesHandler userRole) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity initialEntity = new HttpEntity(headers);

        HttpEntity<UserRoleResponse> rolesResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/", HttpMethod.GET, initialEntity, UserRoleResponse.class);

        if (rolesResponse.hasBody()) {
            UserRoleResponse roles = rolesResponse.getBody();
            if (roles.getRoles().contains("ROLE_BUYER") && !userRole.isBuyer()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_BUYER/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_BUYER") && userRole.isBuyer()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_BUYER");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }

            if (roles.getRoles().contains("ROLE_SELLER") && !userRole.isSeller()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_SELLER/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_SELLER") && userRole.isSeller()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_SELLER");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }

            if (roles.getRoles().contains("ROLE_ADMIN") && !userRole.isAdmin()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_ADMIN/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_ADMIN") && userRole.isAdmin()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_ADMIN");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }
        }

        return "redirect:/userInfo";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        HttpEntity<AppUserResponse[]> usersResponse = restTemplate.exchange(usersApi, HttpMethod.GET, entity, AppUserResponse[].class);

        List<AppUserResponse> users = Arrays.asList(usersResponse.getBody());

        model.addAttribute("users", users);

        return "usersPage";
    }

    @GetMapping("/users/{username}")
    public String getUserInfo(Model model, @PathVariable String username, Principal principal, HttpServletRequest request) {

        Authentication authentication = (Authentication) principal;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        HttpEntity<AppUserResponse> userResponse = restTemplate.exchange(usersApi + username + "/", HttpMethod.GET, entity, AppUserResponse.class);

        model.addAttribute("user", userResponse.getBody());

        boolean sameUser = false;

        if (authentication.getPrincipal() != "anonymousUser") {
            User loggedUser = (User) authentication.getPrincipal();

            sameUser = loggedUser.getUsername().equals(username);
            if (request.isUserInRole("ROLE_ADMIN")) {
                HttpEntity<UserRoleResponse> rolesResponse = restTemplate.exchange(usersApi + username + "/roles/", HttpMethod.GET, entity, UserRoleResponse.class);

                UserRoleResponse userRoles = rolesResponse.getBody();
                model.addAttribute("roles", userRoles);
            }
        }

        HttpEntity<RatingsResponse> ratingsResponse = restTemplate.exchange(usersApi + username + "/ratings/", HttpMethod.GET, entity, RatingsResponse.class);

        model.addAttribute("ratings", ratingsResponse.getBody());
        model.addAttribute("sameUser", sameUser);

        return "userPage";
    }

    @PostMapping(value = "/users/{username}/updateRoles", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateUserRoles(UserRolesHandler userRole) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity initialEntity = new HttpEntity(headers);

        HttpEntity<UserRoleResponse> rolesResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/", HttpMethod.GET, initialEntity, UserRoleResponse.class);

        if (rolesResponse.hasBody()) {
            UserRoleResponse roles = rolesResponse.getBody();
            if (roles.getRoles().contains("ROLE_BUYER") && !userRole.isBuyer()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_BUYER/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_BUYER") && userRole.isBuyer()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_BUYER");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }

            if (roles.getRoles().contains("ROLE_SELLER") && !userRole.isSeller()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_SELLER/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_SELLER") && userRole.isSeller()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_SELLER");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }

            if (roles.getRoles().contains("ROLE_ADMIN") && !userRole.isAdmin()) {
                HttpEntity entity = new HttpEntity(headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/ROLE_ADMIN/",
                        HttpMethod.DELETE, entity, UserRoleResponse.class);
            } else if (!roles.getRoles().contains("ROLE_ADMIN") && userRole.isAdmin()) {
                Map<String, String> request = new HashMap<String, String>(){
                    {
                        put("roleName", "ROLE_ADMIN");
                    }
                };
                HttpEntity entity = new HttpEntity(request, headers);
                HttpEntity<UserRoleResponse> buyerResponse = restTemplate.exchange(usersApi + userRole.getUsername() + "/roles/",
                        HttpMethod.PUT, entity, UserRoleResponse.class);
            }
        }

        return "redirect:/users/" + userRole.getUsername();
    }

    @PostMapping("/users/{username}/delete")
    public String deleteUser(@PathVariable String username) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        restTemplate.exchange(usersApi + username + "/", HttpMethod.DELETE, entity, void.class);

        return "redirect:/users";
    }

    @GetMapping("/updateInfo")
    public String updateInfo(Model model, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity(jSessionId.getJSessionId());

        HttpEntity<AppUserResponse> response = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/", HttpMethod.GET, entity, AppUserResponse.class);

        if (response.hasBody()) {
            AppUserResponse user = response.getBody();
            model.addAttribute("user", user);
        }

        return "updateInfoPage";
    }

    @PostMapping(value = "/updateInfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateInfoDB(AppUserPatchRequest patch, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        HttpClient httpClient = HttpClientBuilder.create().build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(patch, headers);

        if (patch.getPassword().equals("")) {
            patch.setPassword(null);
        }

        HttpEntity<AppUserResponse> response = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/", HttpMethod.PATCH, entity, AppUserResponse.class);

        return "redirect:/userInfo";
    }

    @GetMapping("/addMoney")
    public String addMoney() {
        return "addMoneyPage";
    }

    @PostMapping(value = "/addMoney", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addMoneyDB(@RequestParam("money") Double money, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();

        HttpClient httpClient = HttpClientBuilder.create().build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        HttpEntity<AppUserResponse> response = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/", HttpMethod.GET, entity, AppUserResponse.class);

        AppUserResponse user = response.getBody();

        Map<String, Double> body = new HashMap<>();
        body.put("money", user.getMoney() + money);

        HttpEntity addMoney = new HttpEntity(body, headers);

        HttpEntity<AppUserResponse> moneyResponse = restTemplate.exchange(usersApi + loggedUser.getUsername() + "/", HttpMethod.PATCH, addMoney, AppUserResponse.class);

        return "redirect:/userInfo";
    }
}
