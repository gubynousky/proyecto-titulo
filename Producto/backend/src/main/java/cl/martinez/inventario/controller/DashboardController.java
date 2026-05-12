package cl.martinez.inventario.controller;

import cl.martinez.inventario.dto.DashboardDTO;
import cl.martinez.inventario.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> obtener() {
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}
