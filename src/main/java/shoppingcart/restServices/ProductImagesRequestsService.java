package shoppingcart.restServices;

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
import shoppingcart.repository.JpaAppUserRepository;
import shoppingcart.repository.JpaProductImagesRepository;
import shoppingcart.repository.JpaProductsRepository;
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

    // GET

    public List<ProductImagesResponse> getProductImages(Long productId) {
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
        // TODO: only admin or owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = appUserRepo.findByUsername(((User) authentication.getPrincipal()).getUsername());

        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        Path currentPath = Paths.get(".");
        Path absolutePath = currentPath.toAbsolutePath();

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
                .path(absolutePath + "/src/main/resources/images/" + user.getUsername() + "/")
                .product(product)
                .build();

        ProductImages generateId = imagesRepo.save(newImage);

        String checkDirectories = generateId.getPath();

        generateId.setPath(generateId.getPath() + productId + "_" + generateId.getProductImageId() + "." + extension.get());

        ProductImages saved = imagesRepo.save(generateId);

        boolean dirExists = Files.exists(Paths.get(checkDirectories));

        try {
            if(!dirExists) {
                Files.createDirectories(Paths.get(checkDirectories));
            }

            byte[] bytes = imageFile.getBytes();
            Path path = Paths.get(saved.getPath());
            System.out.println(path);
            Files.write(path, bytes);
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

    public ResponseEntity <HttpStatus> deleteImage(Long productId, Long productImageId) {
        Products product = productsRepo.findByProductId(productId);
        if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        ProductImages image = imagesRepo.findByProductImageId(productImageId);
        if (image == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");

        Path path = Paths.get(image.getPath());
        imagesRepo.delete(image);

        try {
            Files.delete(path);
        } catch (IOException ioExceptionObj) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
