package cl.martinez.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardDTO {
    private Long totalProductos;
    private Long productosBajoStock;
    private Long ventasHoy;
    private BigDecimal ingresosHoy;
    private Long ventasMes;
    private BigDecimal ingresosMes;
    private List<TopProducto> topProductos;
    private List<VentaPorDia> ventasPorDia;

    @Data
    public static class TopProducto {
        private Long productoId;
        private String nombre;
        private Long cantidadVendida;
    }

    @Data
    public static class VentaPorDia {
        private String fecha;
        private Long cantidad;
        private BigDecimal total;
    }
}
