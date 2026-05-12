package cl.martinez.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0)
    private Integer stockMinimo;

    private Long categoriaId;
    private String categoriaNombre;
    private Boolean activo;
}
