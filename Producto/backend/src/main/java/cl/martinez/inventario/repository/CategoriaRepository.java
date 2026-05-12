package cl.martinez.inventario.repository;

import cl.martinez.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivaTrue();
    Boolean existsByNombre(String nombre);
}
