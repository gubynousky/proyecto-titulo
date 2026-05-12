package cl.martinez.inventario.repository;

import cl.martinez.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByActivoTrue(Pageable pageable);
    Page<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Optional<Producto> findByCodigo(String codigo);
    Boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    List<Producto> findProductosBajoStock();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    Long countProductosActivos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    Long countProductosBajoStock();
}
