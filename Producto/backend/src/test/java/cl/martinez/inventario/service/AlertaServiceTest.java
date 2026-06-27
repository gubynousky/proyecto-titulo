package cl.martinez.inventario.service;

import cl.martinez.inventario.model.Alerta;
import cl.martinez.inventario.model.Categoria;
import cl.martinez.inventario.model.Producto;
import cl.martinez.inventario.repository.AlertaRepository;
import cl.martinez.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaService — Pruebas Unitarias")
class AlertaServiceTest {

    @Mock private AlertaRepository alertaRepository;
    @Mock private ProductoRepository productoRepository;
    @InjectMocks private AlertaService alertaService;

    private Producto crearProducto(Long id, String nombre, int stock, int stockMinimo) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setCodigo("COD" + id);
        p.setPrecio(new BigDecimal("1000"));
        p.setStock(stock);
        p.setStockMinimo(stockMinimo);
        p.setActivo(true);
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Test");
        p.setCategoria(cat);
        return p;
    }

    @Test
    @DisplayName("Genera alerta CRITICO cuando stock = 0")
    void generarAlerta_sinStock() {
        Producto sinStock = crearProducto(1L, "Atún", 0, 8);
        when(productoRepository.findProductosBajoStock()).thenReturn(List.of(sinStock));
        when(alertaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        alertaService.generarAlertas();

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        Alerta alerta = captor.getValue();
        assertEquals(Alerta.NivelAlerta.CRITICO, alerta.getNivel());
        assertFalse(alerta.getLeida());
        assertEquals(sinStock, alerta.getProducto());
        assertTrue(alerta.getMensaje().contains("SIN STOCK"));
    }

    @Test
    @DisplayName("Genera alerta ALTO cuando stock <= stockMinimo/2")
    void generarAlerta_stockCritico() {
        Producto critico = crearProducto(2L, "Jabón Dove", 1, 6);
        when(productoRepository.findProductosBajoStock()).thenReturn(List.of(critico));
        when(alertaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        alertaService.generarAlertas();

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertEquals(Alerta.NivelAlerta.ALTO, captor.getValue().getNivel());
        assertTrue(captor.getValue().getMensaje().contains("crítico"));
    }

    @Test
    @DisplayName("Genera alerta MEDIO cuando stock < stockMinimo pero > stockMinimo/2")
    void generarAlerta_stockBajo() {
        Producto bajo = crearProducto(3L, "Oreo", 7, 10);
        when(productoRepository.findProductosBajoStock()).thenReturn(List.of(bajo));
        when(alertaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        alertaService.generarAlertas();

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertEquals(Alerta.NivelAlerta.MEDIO, captor.getValue().getNivel());
        assertTrue(captor.getValue().getMensaje().contains("bajo"));
    }

    @Test
    @DisplayName("No genera alertas cuando no hay productos bajo stock")
    void noGeneraAlerta_listaVacia() {
        when(productoRepository.findProductosBajoStock()).thenReturn(List.of());

        alertaService.generarAlertas();

        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Genera múltiples alertas para múltiples productos")
    void generarAlertas_multiples() {
        Producto sinStock = crearProducto(1L, "Atún", 0, 8);
        Producto critico = crearProducto(2L, "Jabón", 1, 6);
        Producto bajo = crearProducto(3L, "Oreo", 7, 10);

        when(productoRepository.findProductosBajoStock()).thenReturn(List.of(sinStock, critico, bajo));
        when(alertaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        alertaService.generarAlertas();

        verify(alertaRepository, times(3)).save(any());
    }
}
