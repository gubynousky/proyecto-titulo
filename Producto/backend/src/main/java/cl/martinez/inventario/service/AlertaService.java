package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.AlertaDTO;
import cl.martinez.inventario.model.*;
import cl.martinez.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final ProductoRepository productoRepository;

    public void generarAlertas() {
        List<Producto> productosBajos = productoRepository.findProductosBajoStock();
        for (Producto p : productosBajos) {
            Alerta.NivelAlerta nivel;
            String mensaje;
            if (p.getStock() == 0) {
                nivel = Alerta.NivelAlerta.CRITICO;
                mensaje = "SIN STOCK: " + p.getNombre() + " (código: " + p.getCodigo() + ")";
            } else if (p.getStock() <= p.getStockMinimo() / 2) {
                nivel = Alerta.NivelAlerta.ALTO;
                mensaje = "Stock crítico: " + p.getNombre() + " - Quedan " + p.getStock() + " unidades";
            } else {
                nivel = Alerta.NivelAlerta.MEDIO;
                mensaje = "Stock bajo: " + p.getNombre() + " - Quedan " + p.getStock() + " unidades (mínimo: " + p.getStockMinimo() + ")";
            }

            Alerta alerta = Alerta.builder()
                    .producto(p)
                    .nivel(nivel)
                    .mensaje(mensaje)
                    .leida(false)
                    .build();
            alertaRepository.save(alerta);
        }
    }

    public List<AlertaDTO> obtenerNoLeidas() {
        return alertaRepository.findByLeidaFalseOrderByCreatedAtDesc().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public Long contarNoLeidas() {
        return alertaRepository.countByLeidaFalse();
    }

    public void marcarComoLeida(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alerta.setLeida(true);
        alertaRepository.save(alerta);
    }

    public void marcarTodasComoLeidas() {
        List<Alerta> noLeidas = alertaRepository.findByLeidaFalseOrderByCreatedAtDesc();
        noLeidas.forEach(a -> a.setLeida(true));
        alertaRepository.saveAll(noLeidas);
    }

    private AlertaDTO toDTO(Alerta a) {
        AlertaDTO dto = new AlertaDTO();
        dto.setId(a.getId());
        dto.setProductoId(a.getProducto().getId());
        dto.setProductoNombre(a.getProducto().getNombre());
        dto.setNivel(a.getNivel().name());
        dto.setMensaje(a.getMensaje());
        dto.setLeida(a.getLeida());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
