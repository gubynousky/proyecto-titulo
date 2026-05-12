package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.ProductoDTO;
import cl.martinez.inventario.exception.*;
import cl.martinez.inventario.model.*;
import cl.martinez.inventario.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public Page<ProductoDTO> listar(String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Page<Producto> productos;
        if (nombre != null && !nombre.isEmpty()) {
            productos = productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre, pageable);
        } else {
            productos = productoRepository.findByActivoTrue(pageable);
        }
        return productos.map(this::toDTO);
    }

    public ProductoDTO obtenerPorId(Long id) {
        return toDTO(productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id)));
    }

    public ProductoDTO crear(ProductoDTO dto) {
        if (productoRepository.existsByCodigo(dto.getCodigo())) {
            throw new BadRequestException("Ya existe un producto con el código: " + dto.getCodigo());
        }
        Producto producto = new Producto();
        mapDtoToEntity(dto, producto);
        return toDTO(productoRepository.save(producto));
    }

    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        var existente = productoRepository.findByCodigo(dto.getCodigo());
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new BadRequestException("Ya existe otro producto con el código: " + dto.getCodigo());
        }
        mapDtoToEntity(dto, producto);
        return toDTO(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private void mapDtoToEntity(ProductoDTO dto, Producto p) {
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 10);
        if (dto.getCategoriaId() != null) {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            p.setCategoria(cat);
        }
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setStock(p.getStock());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setActivo(p.getActivo());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        return dto;
    }
}
