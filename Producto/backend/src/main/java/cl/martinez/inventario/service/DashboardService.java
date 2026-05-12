package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.DashboardDTO;
import cl.martinez.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public DashboardDTO obtenerDashboard() {
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalProductos(productoRepository.countProductosActivos());
        dto.setProductosBajoStock(productoRepository.countProductosBajoStock());

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        dto.setVentasHoy(ventaRepository.countVentasPorPeriodo(inicioHoy, finHoy));
        dto.setIngresosHoy(ventaRepository.sumTotalVentasPorPeriodo(inicioHoy, finHoy));

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        dto.setVentasMes(ventaRepository.countVentasPorPeriodo(inicioMes, finHoy));
        dto.setIngresosMes(ventaRepository.sumTotalVentasPorPeriodo(inicioMes, finHoy));

        List<Object[]> top = detalleVentaRepository.findTopProductosVendidos();
        dto.setTopProductos(top.stream().limit(5).map(row -> {
            DashboardDTO.TopProducto tp = new DashboardDTO.TopProducto();
            tp.setProductoId((Long) row[0]);
            tp.setNombre((String) row[1]);
            tp.setCantidadVendida((Long) row[2]);
            return tp;
        }).collect(Collectors.toList()));

        List<DashboardDTO.VentaPorDia> ventasPorDia = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        for (int i = 6; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            LocalDateTime inicio = dia.atStartOfDay();
            LocalDateTime fin = dia.atTime(LocalTime.MAX);
            DashboardDTO.VentaPorDia vpd = new DashboardDTO.VentaPorDia();
            vpd.setFecha(dia.format(fmt));
            vpd.setCantidad(ventaRepository.countVentasPorPeriodo(inicio, fin));
            vpd.setTotal(ventaRepository.sumTotalVentasPorPeriodo(inicio, fin));
            ventasPorDia.add(vpd);
        }
        dto.setVentasPorDia(ventasPorDia);

        return dto;
    }
}
