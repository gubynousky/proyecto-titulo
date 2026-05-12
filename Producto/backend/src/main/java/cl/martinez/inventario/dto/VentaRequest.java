package cl.martinez.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {
    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<DetalleRequest> detalles;

    @Data
    public static class DetalleRequest {
        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
    }
}
