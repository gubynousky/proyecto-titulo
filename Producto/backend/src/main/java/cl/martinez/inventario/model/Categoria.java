package cl.martinez.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activa = true;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    @PrePersist
    protected void onCreate() {
        if (this.activa == null) this.activa = true;
    }
}
