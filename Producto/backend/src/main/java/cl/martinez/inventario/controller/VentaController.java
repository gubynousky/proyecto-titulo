package cl.martinez.inventario.controller;

import cl.martinez.inventario.dto.*;
import cl.martinez.inventario.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(request, auth.getName()));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anular(id));
    }
}
