package com.uade.tpo.tienda.service.descuento;

import com.uade.tpo.tienda.dto.*;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.entity.Descuento;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.DescuentoInvalidoException;
import com.uade.tpo.tienda.exceptions.RecursoNoEncontradoException;
import com.uade.tpo.tienda.repository.CategoryRepository;
import com.uade.tpo.tienda.repository.DescuentoRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DescuentoServiceImpl implements DescuentoService {

    @Autowired
    private DescuentoRepository descuentoRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    

    @Override
    @Transactional
    public DescuentoResponse crearDescuento(DescuentoRequest request) {
        // Verificar si ya existe un descuento con el mismo código
        if (descuentoRepository.existsByCodigo(request.getCodigo())) {
            throw new DescuentoInvalidoException("Ya existe un descuento con el código: " + request.getCodigo());
        }
        
        // Verificar que el porcentaje esté en el rango correcto
        if (request.getPorcentaje() <= 0 || request.getPorcentaje() > 100) {
            throw new DescuentoInvalidoException("El porcentaje de descuento debe estar entre 0 y 100");
        }
        
        // Obtener la categoría si se especificó
        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoryRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.getCategoriaId()));
        }
        
        // Crear nuevo descuento
        Descuento descuento = Descuento.builder()
                .codigo(request.getCodigo().toUpperCase())
                .porcentaje(request.getPorcentaje())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .montoMinimo(request.getMontoMinimo())
                .categoriaAplicable(categoria)
                .build();
        
        // Guardar y retornar
        Descuento guardado = descuentoRepository.save(descuento);
        return mapToDescuentoResponse(guardado);
    }

    @Override
    @Transactional
    public DescuentoResponse actualizarDescuento(Long id, DescuentoRequest request) {
        Descuento descuento = descuentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Descuento no encontrado con ID: " + id));
        
        // Verificar código único si se cambió
        if (!descuento.getCodigo().equals(request.getCodigo()) && 
            descuentoRepository.existsByCodigo(request.getCodigo())) {
            throw new DescuentoInvalidoException("Ya existe un descuento con el código: " + request.getCodigo());
        }
        
        // Verificar porcentaje
        if (request.getPorcentaje() <= 0 || request.getPorcentaje() > 100) {
            throw new DescuentoInvalidoException("El porcentaje de descuento debe estar entre 0 y 100");
        }
        
        // Obtener la categoría si se especificó
        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoryRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.getCategoriaId()));
        }
        
        // Actualizar descuento
        descuento.setCodigo(request.getCodigo().toUpperCase());
        descuento.setPorcentaje(request.getPorcentaje());
        descuento.setDescripcion(request.getDescripcion());
        descuento.setFechaInicio(request.getFechaInicio());
        descuento.setFechaFin(request.getFechaFin());
        descuento.setMontoMinimo(request.getMontoMinimo());
        descuento.setCategoriaAplicable(categoria);
        
        // Guardar y retornar
        Descuento actualizado = descuentoRepository.save(descuento);
        return mapToDescuentoResponse(actualizado);
    }

    @Override
    @Transactional
    public MessageResponse eliminarDescuento(Long id) {
        Descuento descuento = descuentoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Descuento no encontrado con ID: " + id));
        String codigo = descuento.getCodigo();
        
        descuentoRepository.deleteById(id);
        
        return MessageResponse.builder()
            .mensaje("Descuento '" + codigo + "' eliminado correctamente")
            .estado("success")
            .timestamp(LocalDateTime.now())
            .build();
    }
    @Override
    public List<DescuentoResponse> obtenerTodosDescuentos() {
        return descuentoRepository.findAll().stream()
                .map(this::mapToDescuentoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DescuentoResponse obtenerDescuentoPorId(Long id) {
        Descuento descuento = descuentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Descuento no encontrado con ID: " + id));
        
        return mapToDescuentoResponse(descuento);
    }

    @Override
    @Transactional
    public DescuentoResponse cambiarEstadoDescuento(Long id, boolean activo) {
        Descuento descuento = descuentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Descuento no encontrado con ID: " + id));
        
        descuento.setActivo(activo);
        Descuento actualizado = descuentoRepository.save(descuento);
        
        return mapToDescuentoResponse(actualizado);
    }

    @Override
    public DescuentoAplicadoResponse validarDescuento(ValidarDescuentoRequest request) {
        // Si no se proporciona código, retornar sin descuento
        if (request.getCodigoDescuento() == null || request.getCodigoDescuento().isEmpty()) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .mensajeError("No se ha proporcionado un código de descuento")
                    .build();
        }
        
        // Buscar descuento por código
        Descuento descuento = descuentoRepository.findByCodigo(request.getCodigoDescuento().toUpperCase())
                .orElse(null);
        
        // Si no existe o no está vigente
        if (descuento == null) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .mensajeError("Código de descuento no encontrado")
                    .build();
        }
        
        if (!descuento.isActivo()) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .mensajeError("El código de descuento no está activo")
                    .build();
        }
        
        if (!descuento.isVigente()) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .mensajeError("El código de descuento ha expirado o aún no está vigente")
                    .build();
        }
        
        // Calcular el subtotal del carrito
        double subtotal = 0.0;
        double subtotalAplicable = 0.0;
        
        // Si el carrito está vacío
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .mensajeError("El carrito está vacío")
                    .build();
        }
        
        // Calcular el subtotal y verificar si aplica descuento por categoría
        for (CompraItemRequest item : request.getItems()) {
            Producto producto = productRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + item.getProductoId()));
            
            double itemSubtotal = producto.getPrecio() * item.getCantidad();
            subtotal += itemSubtotal;
            
            // Si el descuento aplica a una categoría específica, solo contar productos de esa categoría
            if (descuento.getCategoriaAplicable() != null && 
                producto.getCategoria().getId().equals(descuento.getCategoriaAplicable().getId())) {
                subtotalAplicable += itemSubtotal;
            } else if (descuento.getCategoriaAplicable() == null) {
                // Si no hay categoría específica, todo suma al subtotal aplicable
                subtotalAplicable += itemSubtotal;
            }
        }
        
        // Verificar monto mínimo
        if (descuento.getMontoMinimo() != null && subtotal < descuento.getMontoMinimo()) {
            return DescuentoAplicadoResponse.builder()
                    .descuentoAplicado(false)
                    .subtotalSinDescuento(subtotal)
                    .mensajeError("El monto mínimo para aplicar este descuento es $" + descuento.getMontoMinimo())
                    .build();
        }
        
        // Calcular descuento
        double montoDescuento = 0;
        if (descuento.getCategoriaAplicable() != null) {
            // Si aplica a categoría específica, solo aplicar sobre productos de esa categoría
            montoDescuento = subtotalAplicable * (descuento.getPorcentaje() / 100);
        } else {
            // Aplicar sobre todo el carrito
            montoDescuento = subtotal * (descuento.getPorcentaje() / 100);
        }
        
        double totalConDescuento = subtotal - montoDescuento;
        
        // Retornar respuesta con descuento aplicado
        return DescuentoAplicadoResponse.builder()
                .descuentoAplicado(true)
                .codigo(descuento.getCodigo())
                .porcentajeDescuento(descuento.getPorcentaje())
                .subtotalSinDescuento(subtotal)
                .montoDescuento(montoDescuento)
                .totalConDescuento(totalConDescuento)
                .build();
    }
    
    /**
     * Convierte una entidad Descuento a su DTO correspondiente
     */
    private DescuentoResponse mapToDescuentoResponse(Descuento descuento) {
        return DescuentoResponse.builder()
                .id(descuento.getId())
                .codigo(descuento.getCodigo())
                .porcentaje(descuento.getPorcentaje())
                .descripcion(descuento.getDescripcion())
                .fechaInicio(descuento.getFechaInicio())
                .fechaFin(descuento.getFechaFin())
                .activo(descuento.isActivo())
                .vigente(descuento.isVigente())
                .montoMinimo(descuento.getMontoMinimo())
                .categoria(descuento.getCategoriaAplicable() != null ? 
                        descuento.getCategoriaAplicable().getNombre() : null)
                .build();
    }
}