package cl.martinez.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.leida == null) this.leida = false;
    }

    public enum NivelAlerta {
        CRITICO, ALTO, MEDIO, BAJO
    }
}
