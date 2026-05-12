package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.PrediccionDTO;
import cl.martinez.inventario.model.Producto;
import cl.martinez.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrediccionService {

    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public List<PrediccionDTO> predecirStock() {
        List<Producto> productos = productoRepository.findAll().stream()
                .filter(Producto::getActivo).collect(Collectors.toList());

        return productos.stream().map(this::calcularPrediccion)
                .sorted(Comparator.comparingInt(PrediccionDTO::getDiasRestantes))
                .collect(Collectors.toList());
    }

    public PrediccionDTO predecirProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return calcularPrediccion(producto);
    }

    private PrediccionDTO calcularPrediccion(Producto producto) {
        double[] pesos = {0.4, 0.3, 0.2, 0.1};
        double consumoPonderado = 0;
        boolean tieneVentas = false;

        for (int i = 0; i < 4; i++) {
            LocalDate inicioSemana = LocalDate.now().minusWeeks(i + 1);
            LocalDate finSemana = LocalDate.now().minusWeeks(i);
            Long vendido = detalleVentaRepository.sumCantidadVendidaPorProductoYPeriodo(
                    producto.getId(), inicioSemana.atStartOfDay(), finSemana.atTime(LocalTime.MAX));
            if (vendido != null && vendido > 0) {
                tieneVentas = true;
                consumoPonderado += (vendido.doubleValue() / 7.0) * pesos[i];
            }
        }

        PrediccionDTO dto = new PrediccionDTO();
        dto.setProductoId(producto.getId());
        dto.setProductoNombre(producto.getNombre());
        dto.setStockActual(producto.getStock());
        dto.setStockMinimo(producto.getStockMinimo());

        if (!tieneVentas || consumoPonderado <= 0) {
            dto.setConsumoPromedioDiario(BigDecimal.ZERO);
            dto.setDiasRestantes(999);
            dto.setNivelRiesgo("BAJO");
            dto.setRecomendacion("Sin datos de ventas suficientes para predecir");
        } else {
            BigDecimal consumoDiario = BigDecimal.valueOf(consumoPonderado).setScale(2, RoundingMode.HALF_UP);
            int diasRestantes = (int) (producto.getStock() / consumoPonderado);

            dto.setConsumoPromedioDiario(consumoDiario);
            dto.setDiasRestantes(diasRestantes);

            if (diasRestantes <= 3) {
                dto.setNivelRiesgo("CRITICO");
                dto.setRecomendacion("Reabastecer URGENTE. Stock se agotará en " + diasRestantes + " días");
            } else if (diasRestantes <= 7) {
                dto.setNivelRiesgo("ALTO");
                dto.setRecomendacion("Reabastecer pronto. Stock durará " + diasRestantes + " días");
            } else if (diasRestantes <= 14) {
                dto.setNivelRiesgo("MEDIO");
                dto.setRecomendacion("Planificar reabastecimiento. Stock para " + diasRestantes + " días");
            } else {
                dto.setNivelRiesgo("BAJO");
                dto.setRecomendacion("Stock suficiente para " + diasRestantes + " días");
            }
        }
        return dto;
    }
}
