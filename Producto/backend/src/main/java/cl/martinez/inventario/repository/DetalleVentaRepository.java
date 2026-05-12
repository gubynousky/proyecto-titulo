package cl.martinez.inventario.repository;

import cl.martinez.inventario.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);

    @Query("SELECT dv.producto.id, dv.producto.nombre, SUM(dv.cantidad) as totalVendido " +
           "FROM DetalleVenta dv WHERE dv.venta.anulada = false " +
           "GROUP BY dv.producto.id, dv.producto.nombre ORDER BY totalVendido DESC")
    List<Object[]> findTopProductosVendidos();

    @Query("SELECT SUM(dv.cantidad) FROM DetalleVenta dv WHERE dv.producto.id = :productoId AND dv.venta.anulada = false " +
           "AND dv.venta.createdAt BETWEEN :inicio AND :fin")
    Long sumCantidadVendidaPorProductoYPeriodo(@Param("productoId") Long productoId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}
