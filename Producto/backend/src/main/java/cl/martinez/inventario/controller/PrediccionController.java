package cl.martinez.inventario.controller;

import cl.martinez.inventario.dto.PrediccionDTO;
import cl.martinez.inventario.service.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/predicciones")
@RequiredArgsConstructor
public class PrediccionController {

    private final PrediccionService prediccionService;

    @GetMapping
    public ResponseEntity<List<PrediccionDTO>> predecirTodos() {
        return ResponseEntity.ok(prediccionService.predecirStock());
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<PrediccionDTO> predecirProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(prediccionService.predecirProducto(productoId));
    }
}
