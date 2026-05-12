package cl.martinez.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrediccionDTO {
    private Long productoId;
    private String productoNombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private BigDecimal consumoPromedioDiario;
    private Integer diasRestantes;
    private String nivelRiesgo;
    private String recomendacion;
}
