package shoppingcart.requests;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserRequest {
    // TODO:
    // Falta ponerle los tamaños maximos a cada una de los campos para evitar esos errores a la hora de insertar en base 
    // de datos
    @NotNull(message = "The username is a required field")
    private String username;

    @NotNull(message = "The password is a required field")
    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;
    
    private double money;
}