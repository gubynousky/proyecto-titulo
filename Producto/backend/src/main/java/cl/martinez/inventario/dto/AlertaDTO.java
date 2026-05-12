package cl.martinez.inventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertaDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String nivel;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime createdAt;
}
