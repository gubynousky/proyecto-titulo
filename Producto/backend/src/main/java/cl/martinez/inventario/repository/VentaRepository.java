package cl.martinez.inventario.repository;

import cl.martinez.inventario.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByAnuladaFalseOrderByCreatedAtDesc();

    @Query("SELECT v FROM Venta v WHERE v.anulada = false AND v.createdAt BETWEEN :inicio AND :fin ORDER BY v.createdAt DESC")
    List<Venta> findVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.anulada = false AND v.createdAt BETWEEN :inicio AND :fin")
    BigDecimal sumTotalVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.anulada = false AND v.createdAt BETWEEN :inicio AND :fin")
    Long countVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
