package shoppingcart.controllers;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import shoppingcart.responses.SingleRatingResponse;
import shoppingcart.services.JSessionId;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping
public class RatingsFrontController {
    @Autowired
    JSessionId jSessionId;

    final private String ratingsApi = "http://localhost:8080/api/ratings/";
    final private String usersApi = "http://localhost:8080/api/users/";

    @GetMapping("/users/{seller}/deleteRating/{ratingId}")
    public String deleteRating(@PathVariable Long ratingId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        restTemplate.exchange(ratingsApi + ratingId + "/", HttpMethod.DELETE, entity, void.class);

        return "redirect:/users/{seller}";
    }

    @PostMapping(value = "/users/{seller}/addRating", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addRating(@RequestParam("rating") Integer rating, @PathVariable String seller) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        Map<String, Object> body = new HashMap<>();
        body.put("rating", rating);

        HttpEntity entity = new HttpEntity(body, headers);

        HttpEntity<SingleRatingResponse> ratingResponse = restTemplate.exchange(usersApi + seller + "/ratings/", HttpMethod.PUT, entity, SingleRatingResponse.class);

        return "redirect:/users/{seller}";
    }

    @PostMapping(value = "/users/{seller}/updateRating", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateRatingDB(@RequestParam("ratingId") Long ratingId, @RequestParam("rating") Integer rating) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = jSessionId.getJSessionId();

        Map<String, Object> body = new HashMap<>();
        body.put("rating", rating);

        HttpEntity entity = new HttpEntity(body, headers);

        HttpEntity<SingleRatingResponse> ratingResponse = restTemplate.exchange(ratingsApi + ratingId + "/", HttpMethod.PATCH, entity, SingleRatingResponse.class);

        return "redirect:/users/{seller}";
    }
}
