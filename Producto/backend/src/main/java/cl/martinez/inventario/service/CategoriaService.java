package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.CategoriaDTO;
import cl.martinez.inventario.exception.*;
import cl.martinez.inventario.model.Categoria;
import cl.martinez.inventario.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarActivas() {
        return categoriaRepository.findByActivaTrue().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public CategoriaDTO obtenerPorId(Long id) {
        return toDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id)));
    }

    public CategoriaDTO crear(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activa(true)
                .build();
        return toDTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return toDTO(categoriaRepository.save(categoria));
    }

    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaDTO toDTO(Categoria c) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setActiva(c.getActiva());
        dto.setTotalProductos(c.getProductos() != null ? (int) c.getProductos().stream().filter(p -> p.getActivo()).count() : 0);
        return dto;
    }
}
