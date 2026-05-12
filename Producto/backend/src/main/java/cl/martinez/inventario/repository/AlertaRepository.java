package cl.martinez.inventario.repository;

import cl.martinez.inventario.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByLeidaFalseOrderByCreatedAtDesc();
    Long countByLeidaFalse();
    List<Alerta> findByProductoIdOrderByCreatedAtDesc(Long productoId);
}
