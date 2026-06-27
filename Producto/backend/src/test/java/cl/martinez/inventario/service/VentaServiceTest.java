package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.VentaRequest;
import cl.martinez.inventario.dto.VentaResponse;
import cl.martinez.inventario.exception.BadRequestException;
import cl.martinez.inventario.exception.ResourceNotFoundException;
import cl.martinez.inventario.model.*;
import cl.martinez.inventario.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VentaService — Pruebas Unitarias")
class VentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private VentaService ventaService;

    private Usuario usuario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ruby Test");
        usuario.setEmail("test@correo.com");

        producto = new Producto();
        producto.setId(1L);
        producto.setCodigo("TEST001");
        producto.setNombre("Producto Test");
        producto.setPrecio(new BigDecimal("1000"));
        producto.setStock(50);
        producto.setStockMinimo(10);
        producto.setActivo(true);
    }

    @Test
    @DisplayName("Registrar venta exitosa - descuenta stock y calcula IVA 19%")
    void registrarVenta_exitosa() {
        VentaRequest.DetalleRequest detalle = new VentaRequest.DetalleRequest();
        detalle.setProductoId(1L);
        detalle.setCantidad(5);
        VentaRequest request = new VentaRequest();
        request.setDetalles(List.of(detalle));

        when(usuarioRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> {
            Venta v = i.getArgument(0);
            v.setId(1L);
            return v;
        });

        VentaResponse response = ventaService.registrar(request, "test@correo.com");

        assertNotNull(response);
        assertEquals(45, producto.getStock(), "Stock debe descontar 5 unidades: 50-5=45");
        verify(productoRepository).save(producto);
        verify(ventaRepository).save(any(Venta.class));

        BigDecimal subtotalEsperado = new BigDecimal("5000");
        BigDecimal ivaEsperado = new BigDecimal("950.00");
        BigDecimal totalEsperado = new BigDecimal("5950.00");
        assertEquals(0, subtotalEsperado.compareTo(response.getSubtotal()), "Subtotal: 5 x $1000 = $5000");
        assertEquals(0, ivaEsperado.compareTo(response.getIva()), "IVA 19%: $5000 x 0.19 = $950");
        assertEquals(0, totalEsperado.compareTo(response.getTotal()), "Total: $5000 + $950 = $5950");
    }

    @Test
    @DisplayName("Rechazar venta con stock insuficiente")
    void registrarVenta_stockInsuficiente() {
        producto.setStock(3);

        VentaRequest.DetalleRequest detalle = new VentaRequest.DetalleRequest();
        detalle.setProductoId(1L);
        detalle.setCantidad(10);
        VentaRequest request = new VentaRequest();
        request.setDetalles(List.of(detalle));

        when(usuarioRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(BadRequestException.class,
            () -> ventaService.registrar(request, "test@correo.com"),
            "Debe lanzar BadRequestException por stock insuficiente");

        assertEquals(3, producto.getStock(), "Stock no debe modificarse");
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rechazar venta con producto inexistente")
    void registrarVenta_productoNoExiste() {
        VentaRequest.DetalleRequest detalle = new VentaRequest.DetalleRequest();
        detalle.setProductoId(999L);
        detalle.setCantidad(1);
        VentaRequest request = new VentaRequest();
        request.setDetalles(List.of(detalle));

        when(usuarioRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> ventaService.registrar(request, "test@correo.com"),
            "Debe lanzar ResourceNotFoundException");
    }

    @Test
    @DisplayName("Anular venta restaura stock correctamente")
    void anularVenta_restauraStock() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setAnulada(false);
        venta.setUsuario(usuario);
        venta.setSubtotal(new BigDecimal("5000"));
        venta.setIva(new BigDecimal("950"));
        venta.setTotal(new BigDecimal("5950"));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("1000"));
        detalle.setSubtotal(new BigDecimal("5000"));
        venta.setDetalles(List.of(detalle));

        producto.setStock(45);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        VentaResponse response = ventaService.anular(1L);

        assertTrue(response.getAnulada(), "Venta debe marcarse como anulada");
        assertEquals(50, producto.getStock(), "Stock debe restaurarse: 45+5=50");
    }

    @Test
    @DisplayName("Rechazar anulación de venta ya anulada")
    void anularVenta_yaAnulada() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setAnulada(true);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(BadRequestException.class,
            () -> ventaService.anular(1L),
            "Debe rechazar anulación de venta ya anulada");
    }
}
