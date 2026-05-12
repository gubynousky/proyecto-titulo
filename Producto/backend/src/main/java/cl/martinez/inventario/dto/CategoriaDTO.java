package cl.martinez.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoriaDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private Boolean activa;
    private Integer totalProductos;
}
