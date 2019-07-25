package shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import shoppingcart.dataHandlers.NewProductHandler;
import shoppingcart.dataHandlers.PatchProductHandler;
import shoppingcart.requests.ProductsPatchRequest;
import shoppingcart.requests.ProductsRequest;
import shoppingcart.responses.AppUserResponse;
import shoppingcart.responses.ProductImagesResponse;
import shoppingcart.responses.ProductsResponse;
import shoppingcart.responses.TrustResponse;
import shoppingcart.services.JSessionId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class ProductsFrontController {
    @Autowired
    JSessionId jSessionId;

    final private String productsApi = "http://localhost:8080/api/products/";

    @GetMapping("/add-product")
    public String addProduct() {
        return "addProductPage";
    }

    @PostMapping(value = "/add-product", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String productToDB(NewProductHandler product, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(product, headers);

        HttpEntity<TrustResponse> response = restTemplate.exchange(productsApi, HttpMethod.POST, entity, TrustResponse.class);

        return "redirect:/users/" + loggedUser.getUsername() + "/store";
    }

    @GetMapping("/stores/{seller}/{productId}")
    public String getSpecificProduct(Model model, @PathVariable String seller, @PathVariable Long productId, Principal principal) {
        // TODO: Arreglar para la vista de los usuarios no logeados
        User loggedUser = (User) ((Authentication) principal).getPrincipal();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity headers = new HttpEntity(jSessionId.getJSessionId());
        HttpEntity<ProductsResponse> response = restTemplate.exchange(productsApi + productId + "/", HttpMethod.GET, headers, ProductsResponse.class);

        if (response.hasBody()) {
            ProductsResponse product = response.getBody();

            HttpEntity<ProductImagesResponse[]> responseImage =
                    restTemplate.exchange(productsApi + product.getProductId() + "/images/" , HttpMethod.GET, headers, ProductImagesResponse[].class);

            List<ProductImagesResponse> images = Arrays.asList(responseImage.getBody());

            model.addAttribute("isOwner", loggedUser.getUsername().equals(product.getSeller()));
            model.addAttribute("product", product);
            model.addAttribute("images", images);

            System.out.println(images);
        }

        return "productPage";
    }

    @GetMapping("/stores/{seller}/{productId}/modify")
    public String modifySpecificProduct(Model model, @PathVariable String seller, @PathVariable Long productId, Principal principal) {
        User loggedUser = (User) ((Authentication) principal).getPrincipal();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity headers = new HttpEntity(jSessionId.getJSessionId());
        HttpEntity<ProductsResponse> response = restTemplate.exchange(productsApi + productId + "/", HttpMethod.GET, headers, ProductsResponse.class);

        if (response.hasBody()) {
            ProductsResponse product = response.getBody();

            model.addAttribute("isOwner", loggedUser.getUsername().equals(product.getSeller()));
            model.addAttribute("product", product);
        }

        return "modifyProductPage";
    }

    @PostMapping(value = "/stores/{seller}/{productId}/modify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String modifySpecificProductDB(@PathVariable String seller, @PathVariable Long productId, PatchProductHandler patch) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(patch, headers);

        HttpEntity<ProductsResponse> response = restTemplate.exchange(productsApi + patch.getProductId() + "/", HttpMethod.PATCH, entity, ProductsResponse.class);

        return "redirect:/stores/{seller}/{productId}";
    }

    @PostMapping("/stores/{seller}/{productId}/delete")
    public String deleteSpecificProduct(@PathVariable String seller, @PathVariable Long productId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        restTemplate.exchange(productsApi + productId + "/", HttpMethod.DELETE, entity, void.class);

        return "redirect:/users/{seller}/store";
    }

    @GetMapping("/stores/{seller}/{productId}/addImage")
    public String addProductImage(@PathVariable String seller, @PathVariable Long productId, Model model) {
        model.addAttribute("seller", seller);
        model.addAttribute("productId", productId);

        return "addProductImagePage";
    }

    @PostMapping(value = "/stores/{seller}/{productId}/addImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveProductImage(@PathVariable String seller, @PathVariable Long productId, @RequestParam("imageFile") MultipartFile image) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("imageFile", new FileSystemResource(convert(image)));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            HttpEntity<ProductImagesResponse> response = restTemplate.postForEntity(productsApi + productId + "/images/", entity, ProductImagesResponse.class);
        } catch (HttpClientErrorException ex) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, String> map = mapper.readValue(ex.getResponseBodyAsString(), Map.class);
                System.out.println(ex.getResponseBodyAsString());
                return "redirect:/stores/{seller}/{productId}";
            } catch (IOException io) {
                return "redirect:/stores/{seller}/{productId}";
            }
        }

        return "redirect:/stores/{seller}/{productId}";
    }

    private static File convert(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convFile;
    }

    @GetMapping("/stores/{seller}/{productId}/deleteImage")
    public String deleteImages(@PathVariable String seller, @PathVariable Long productId, Model model) {
        model.addAttribute("seller", seller);
        model.addAttribute("productId", productId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        HttpEntity<ProductImagesResponse[]> responseImage =
                restTemplate.exchange(productsApi + productId + "/images/" , HttpMethod.GET, entity, ProductImagesResponse[].class);

        List<ProductImagesResponse> images = Arrays.asList(responseImage.getBody());

        model.addAttribute("images", images);
        model.addAttribute("seller", seller);

        return "deleteProductImagePage";
    }

    @PostMapping("/stores/{seller}/{productId}/deleteImage/{imageId}")
    public String deleteImages(@PathVariable String seller, @PathVariable Long productId, @PathVariable Long imageId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = jSessionId.getJSessionId();

        HttpEntity entity = new HttpEntity(headers);

        restTemplate.exchange(productsApi + productId + "/images/" + imageId + "/", HttpMethod.DELETE, entity, void.class);

        return "redirect:/stores/{seller}/{productId}/deleteImage";
    }
}
