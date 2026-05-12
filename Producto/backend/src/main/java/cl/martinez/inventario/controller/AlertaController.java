package cl.martinez.inventario.controller;

import cl.martinez.inventario.dto.AlertaDTO;
import cl.martinez.inventario.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<AlertaDTO>> listarNoLeidas() {
        return ResponseEntity.ok(alertaService.obtenerNoLeidas());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarNoLeidas() {
        return ResponseEntity.ok(Map.of("count", alertaService.contarNoLeidas()));
    }

    @PostMapping("/generar")
    public ResponseEntity<Map<String, String>> generar() {
        alertaService.generarAlertas();
        return ResponseEntity.ok(Map.of("message", "Alertas generadas"));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        alertaService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas() {
        alertaService.marcarTodasComoLeidas();
        return ResponseEntity.noContent().build();
    }
}
