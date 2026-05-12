package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.*;
import cl.martinez.inventario.exception.*;
import cl.martinez.inventario.model.*;
import cl.martinez.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

    @Transactional
    public VentaResponse registrar(VentaRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setDetalles(new ArrayList<>());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (VentaRequest.DetalleRequest dr : request.getDetalles()) {
            Producto producto = productoRepository.findById(dr.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + dr.getProductoId()));

            if (!producto.getActivo()) {
                throw new BadRequestException("El producto " + producto.getNombre() + " no está activo");
            }
            if (producto.getStock() < dr.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para " + producto.getNombre() +
                        ". Disponible: " + producto.getStock() + ", Solicitado: " + dr.getCantidad());
            }

            producto.setStock(producto.getStock() - dr.getCantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(dr.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(producto.getPrecio().multiply(BigDecimal.valueOf(dr.getCantidad())))
                    .build();

            venta.getDetalles().add(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }

        BigDecimal iva = subtotal.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotal);
        venta.setIva(iva);
        venta.setTotal(subtotal.add(iva));

        return toResponse(ventaRepository.save(venta));
    }

    public List<VentaResponse> listar() {
        return ventaRepository.findByAnuladaFalseOrderByCreatedAtDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public VentaResponse obtenerPorId(Long id) {
        return toResponse(ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id)));
    }

    @Transactional
    public VentaResponse anular(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));

        if (venta.getAnulada()) {
            throw new BadRequestException("La venta ya está anulada");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        venta.setAnulada(true);
        return toResponse(ventaRepository.save(venta));
    }

    private VentaResponse toResponse(Venta v) {
        VentaResponse resp = new VentaResponse();
        resp.setId(v.getId());
        resp.setUsuarioNombre(v.getUsuario().getNombre());
        resp.setSubtotal(v.getSubtotal());
        resp.setIva(v.getIva());
        resp.setTotal(v.getTotal());
        resp.setAnulada(v.getAnulada());
        resp.setCreatedAt(v.getCreatedAt());
        if (v.getDetalles() != null) {
            resp.setDetalles(v.getDetalles().stream().map(d -> {
                VentaResponse.DetalleResponse dr = new VentaResponse.DetalleResponse();
                dr.setProductoId(d.getProducto().getId());
                dr.setProductoNombre(d.getProducto().getNombre());
                dr.setCantidad(d.getCantidad());
                dr.setPrecioUnitario(d.getPrecioUnitario());
                dr.setSubtotal(d.getSubtotal());
                return dr;
            }).collect(Collectors.toList()));
        }
        return resp;
    }
}
