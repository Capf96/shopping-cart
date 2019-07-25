package shoppingcart.restServices;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import shoppingcart.models.AppUser;
import shoppingcart.models.ProductImages;
import shoppingcart.models.Products;
import shoppingcart.models.Trust;
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaProductImagesRepository;
import shoppingcart.repository.JpaProductsRepository;
import shoppingcart.repository.JpaTrustRepository;
import shoppingcart.responses.ProductImagesResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImagesRequestsService {
    @Autowired
    JpaAppUserRepository appUserRepo;

    @Autowired
    JpaProductsRepository productsRepo;

    @Autowired
    JpaProductImagesRepository imagesRepo;

    @Autowired
    JpaTrustRepository trustRepo;

    // GET

    public List<ProductImagesResponse> getProductImages(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        if (authentication.getPrincipal() != "anonymousUser" && !product.getVisible()) {
            String username = ((User) authentication.getPrincipal()).getUsername();
            Trust isTrusted = trustRepo.findByTrust_Truster_UsernameAndTrust_Trustee_Username(product.getSeller().getUsername(), username);
            if (isTrusted == null && !product.getSeller().getUsername().equals(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        } else if (!product.getVisible()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        List<ProductImages> images =  imagesRepo.findByProduct_ProductId(productId);

        List<ProductImagesResponse> responseList = new ArrayList<>();
         for (ProductImages image: images) {
             ProductImagesResponse toAdd = ProductImagesResponse.builder()
                     .productImageId(image.getProductImageId())
                     .productId(image.getProduct().getProductId())
                     .path(image.getPath())
                     .build();

             responseList.add(toAdd);
         }

         return responseList;
    }

    // POST

    public ProductImagesResponse saveImage(Long productId, MultipartFile imageFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();
        AppUser user = appUserRepo.findByUsername(username);

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        if (!product.getSeller().getUsername().equals(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't modify the product");

        Path currentPath = Paths.get(".");

        String originalFilename = imageFile.getOriginalFilename();

        Optional<String> extension = Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFilename.lastIndexOf(".") + 1));

        if (!(extension.isPresent() &&
                (extension.get().toLowerCase().equals("jpg") ||
                        extension.get().toLowerCase().equals("jpeg") ||
                        extension.get().toLowerCase().equals("png")))) throw new
                ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only jpg, jpeg or png allowed");

        ProductImages newImage = ProductImages.builder()
                .path( "images/")
                .product(product)
                .build();

        ProductImages generateId = imagesRepo.save(newImage);

        generateId.setPath(generateId.getPath() + productId + "_" + generateId.getProductImageId() + "." + extension.get());

        ProductImages saved = imagesRepo.save(generateId);

        String imageLocation = "./";

        try {
            byte[] bytes = imageFile.getBytes();
            Files.write(Paths.get(imageLocation + saved.getPath()), bytes);
        } catch (IOException ioExceptionObj) {
            imagesRepo.delete(saved);
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
        }

        return ProductImagesResponse.builder()
                .productImageId(saved.getProductImageId())
                .productId(saved.getProduct().getProductId())
                .path(saved.getPath())
                .build();
    }

    // DELETE

    public void deleteImage(Long productId, Long productImageId) {
        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        ProductImages image = imagesRepo.findByProductImageId(productImageId);
        if (image == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");

        Path path = Paths.get("./" + image.getPath());
        imagesRepo.delete(image);

        try {
            Files.delete(path);
        } catch (IOException ioExceptionObj) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
