package cl.martinez.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponse {
    private Long id;
    private String usuarioNombre;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private Boolean anulada;
    private LocalDateTime createdAt;
    private List<DetalleResponse> detalles;

    @Data
    public static class DetalleResponse {
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
