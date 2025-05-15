
package com.uade.tpo.tienda.service.entrega;

import com.uade.tpo.tienda.dto.*;
import com.uade.tpo.tienda.entity.MetodoEntrega;
import com.uade.tpo.tienda.entity.PuntoRetiro;
import com.uade.tpo.tienda.exceptions.RecursoNoEncontradoException;
import com.uade.tpo.tienda.repository.MetodoEntregaRepository;
import com.uade.tpo.tienda.repository.PuntoRetiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregaServiceImpl implements EntregaService {

    @Autowired
    private MetodoEntregaRepository metodoEntregaRepository;
    
    @Autowired
    private PuntoRetiroRepository puntoRetiroRepository;
    
    // Implementación de métodos para MetodoEntrega
    
    @Override
    @Transactional
    public MetodoEntregaResponse crearMetodoEntrega(MetodoEntregaRequest request) {
        MetodoEntrega metodoEntrega = MetodoEntrega.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .costoBase(request.getCostoBase())
                .tiempoEstimadoDias(request.getTiempoEstimadoDias())
                .requiereDireccion(request.getRequiereDireccion())
                .requierePuntoRetiro(request.getRequierePuntoRetiro())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
        
        MetodoEntrega saved = metodoEntregaRepository.save(metodoEntrega);
        return mapToMetodoEntregaResponse(saved);
    }
    
    @Override
    @Transactional
    public MetodoEntregaResponse actualizarMetodoEntrega(Long id, MetodoEntregaRequest request) {
        MetodoEntrega metodoEntrega = metodoEntregaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + id));
        
        metodoEntrega.setNombre(request.getNombre());
        metodoEntrega.setDescripcion(request.getDescripcion());
        metodoEntrega.setCostoBase(request.getCostoBase());
        metodoEntrega.setTiempoEstimadoDias(request.getTiempoEstimadoDias());
        metodoEntrega.setRequiereDireccion(request.getRequiereDireccion());
        metodoEntrega.setRequierePuntoRetiro(request.getRequierePuntoRetiro());
        
        if (request.getActivo() != null) {
            metodoEntrega.setActivo(request.getActivo());
        }
        
        MetodoEntrega updated = metodoEntregaRepository.save(metodoEntrega);
        return mapToMetodoEntregaResponse(updated);
    }
    
    @Override
    @Transactional
    public void eliminarMetodoEntrega(Long id) {
        if (!metodoEntregaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + id);
        }
        metodoEntregaRepository.deleteById(id);
    }
    
    @Override
    public List<MetodoEntregaResponse> obtenerMetodosEntrega() {
        return metodoEntregaRepository.findAll().stream()
                .map(this::mapToMetodoEntregaResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetodoEntregaResponse> obtenerMetodosEntregaActivos() {
        return metodoEntregaRepository.findByActivoTrue().stream()
                .map(this::mapToMetodoEntregaResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public MetodoEntregaResponse obtenerMetodoEntregaPorId(Long id) {
        MetodoEntrega metodoEntrega = metodoEntregaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + id));
        
        return mapToMetodoEntregaResponse(metodoEntrega);
    }
    
    // Implementación de métodos para PuntoRetiro
    
    @Override
    @Transactional
    public PuntoRetiroResponse crearPuntoRetiro(PuntoRetiroRequest request) {
        MetodoEntrega metodoEntrega = metodoEntregaRepository.findById(request.getMetodoEntregaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + request.getMetodoEntregaId()));
        
        if (!metodoEntrega.isRequierePuntoRetiro()) {
            throw new IllegalArgumentException("El método de entrega seleccionado no requiere puntos de retiro");
        }
        
        PuntoRetiro puntoRetiro = PuntoRetiro.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .direccion(request.getDireccion())
                .localidad(request.getLocalidad())
                .provincia(request.getProvincia())
                .codigoPostal(request.getCodigoPostal())
                .horarioAtencion(request.getHorarioAtencion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .coordenadas(request.getCoordenadas())
                .metodoEntrega(metodoEntrega)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
        
        PuntoRetiro saved = puntoRetiroRepository.save(puntoRetiro);
        return mapToPuntoRetiroResponse(saved);
    }
    
    @Override
    @Transactional
    public PuntoRetiroResponse actualizarPuntoRetiro(Long id, PuntoRetiroRequest request) {
        PuntoRetiro puntoRetiro = puntoRetiroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Punto de retiro no encontrado con ID: " + id));
        
        MetodoEntrega metodoEntrega = null;
        if (request.getMetodoEntregaId() != null) {
            metodoEntrega = metodoEntregaRepository.findById(request.getMetodoEntregaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + request.getMetodoEntregaId()));
            
            if (!metodoEntrega.isRequierePuntoRetiro()) {
                throw new IllegalArgumentException("El método de entrega seleccionado no requiere puntos de retiro");
            }
        }
        
        puntoRetiro.setNombre(request.getNombre());
        puntoRetiro.setDescripcion(request.getDescripcion());
        puntoRetiro.setDireccion(request.getDireccion());
        puntoRetiro.setLocalidad(request.getLocalidad());
        puntoRetiro.setProvincia(request.getProvincia());
        puntoRetiro.setCodigoPostal(request.getCodigoPostal());
        puntoRetiro.setHorarioAtencion(request.getHorarioAtencion());
        puntoRetiro.setTelefono(request.getTelefono());
        puntoRetiro.setEmail(request.getEmail());
        puntoRetiro.setCoordenadas(request.getCoordenadas());
        
        if (metodoEntrega != null) {
            puntoRetiro.setMetodoEntrega(metodoEntrega);
        }
        
        if (request.getActivo() != null) {
            puntoRetiro.setActivo(request.getActivo());
        }
        
        PuntoRetiro updated = puntoRetiroRepository.save(puntoRetiro);
        return mapToPuntoRetiroResponse(updated);
    }
    
    @Override
    @Transactional
    public void eliminarPuntoRetiro(Long id) {
        if (!puntoRetiroRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Punto de retiro no encontrado con ID: " + id);
        }
        puntoRetiroRepository.deleteById(id);
    }
    
    @Override
    public List<PuntoRetiroResponse> obtenerPuntosRetiro() {
        return puntoRetiroRepository.findAll().stream()
                .map(this::mapToPuntoRetiroResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PuntoRetiroResponse> obtenerPuntosRetiroActivos() {
        return puntoRetiroRepository.findByActivoTrue().stream()
                .map(this::mapToPuntoRetiroResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PuntoRetiroResponse> obtenerPuntosRetiroPorMetodo(Long metodoEntregaId) {
        MetodoEntrega metodoEntrega = metodoEntregaRepository.findById(metodoEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + metodoEntregaId));
        
        return puntoRetiroRepository.findByMetodoEntregaAndActivoTrue(metodoEntrega).stream()
                .map(this::mapToPuntoRetiroResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PuntoRetiroResponse obtenerPuntoRetiroPorId(Long id) {
        PuntoRetiro puntoRetiro = puntoRetiroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Punto de retiro no encontrado con ID: " + id));
        
        return mapToPuntoRetiroResponse(puntoRetiro);
    }
    
    // Métodos auxiliares de mapeo
    
    private MetodoEntregaResponse mapToMetodoEntregaResponse(MetodoEntrega metodoEntrega) {
        return MetodoEntregaResponse.builder()
                .id(metodoEntrega.getId())
                .nombre(metodoEntrega.getNombre())
                .descripcion(metodoEntrega.getDescripcion())
                .costoBase(metodoEntrega.getCostoBase())
                .tiempoEstimadoDias(metodoEntrega.getTiempoEstimadoDias())
                .requiereDireccion(metodoEntrega.isRequiereDireccion())
                .requierePuntoRetiro(metodoEntrega.isRequierePuntoRetiro())
                .activo(metodoEntrega.isActivo())
                .build();
    }
    
    private PuntoRetiroResponse mapToPuntoRetiroResponse(PuntoRetiro puntoRetiro) {
        return PuntoRetiroResponse.builder()
                .id(puntoRetiro.getId())
                .nombre(puntoRetiro.getNombre())
                .descripcion(puntoRetiro.getDescripcion())
                .direccion(puntoRetiro.getDireccion())
                .localidad(puntoRetiro.getLocalidad())
                .provincia(puntoRetiro.getProvincia())
                .codigoPostal(puntoRetiro.getCodigoPostal())
                .horarioAtencion(puntoRetiro.getHorarioAtencion())
                .telefono(puntoRetiro.getTelefono())
                .email(puntoRetiro.getEmail())
                .coordenadas(puntoRetiro.getCoordenadas())
                .metodoEntrega(puntoRetiro.getMetodoEntrega() != null ? puntoRetiro.getMetodoEntrega().getNombre() : null)
                .metodoEntregaId(puntoRetiro.getMetodoEntrega() != null ? puntoRetiro.getMetodoEntrega().getId() : null)
                .activo(puntoRetiro.isActivo())
                .build();
    }
}