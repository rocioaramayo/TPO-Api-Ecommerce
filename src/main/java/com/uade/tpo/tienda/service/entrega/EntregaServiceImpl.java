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
import java.util.Optional;
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
        // Control único para método de retiro
        if (request.getRequierePuntoRetiro() != null && request.getRequierePuntoRetiro()) {
            Optional<MetodoEntrega> existente = metodoEntregaRepository.findFirstByRequierePuntoRetiroTrue();
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Ya existe un método de entrega de tipo Punto de Retiro");
            }
        }

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
    public void activarMetodoDeEntrega(Long id){
        Optional<MetodoEntrega> metodOptional = metodoEntregaRepository.findById(id); 
        if (!metodOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + id);
        }
        MetodoEntrega metodo = metodOptional.get();
        metodo.setActivo(true);
        metodoEntregaRepository.save(metodo);
    }

    @Override
    @Transactional
    public void eliminarMetodoEntrega(Long id) {
        Optional<MetodoEntrega> metodOptional = metodoEntregaRepository.findById(id);

        if (!metodOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Método de entrega no encontrado con ID: " + id);
        }
        MetodoEntrega metodo = metodOptional.get();
        metodo.setActivo(false);
        metodoEntregaRepository.save(metodo);
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
    public void activarPuntoRetiro(Long id){
        Optional<PuntoRetiro> puntoOptional = puntoRetiroRepository.findById(id); 
        if (!puntoOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Punto de retiro no encontrado con ID: " + id);
        }
        PuntoRetiro punto = puntoOptional.get();
        punto.setActivo(true);
        puntoRetiroRepository.save(punto);
    }

    @Override
    @Transactional
    public void eliminarPuntoRetiro(Long id) {
        Optional<PuntoRetiro> puntoOptional = puntoRetiroRepository.findById(id);

        if (!puntoOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Punto de entrega no encontrado con ID: " + id);
        }
        PuntoRetiro punto = puntoOptional.get();
        punto.setActivo(false);
        puntoRetiroRepository.save(punto);
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
    @Override
    public CotizacionEnvioResponse cotizarEnvio(CotizacionEnvioRequest request) {
        // Lógica de ejemplo por código postal
        double precio;
        String codigoPostal = request.getCodigoPostal();
        if (codigoPostal == null) {
            precio = 0.0;
        } else if (codigoPostal.startsWith("14")) { // CABA
            precio = 1200.0;
        } else if (codigoPostal.startsWith("16") || codigoPostal.startsWith("17") || codigoPostal.startsWith("18")) { // GBA ejemplo
            precio = 1800.0;
        } else if (codigoPostal.startsWith("5")) { // Córdoba
            precio = 2100.0;
        } else {
            precio = 3200.0; // Resto del país
        }
        return new CotizacionEnvioResponse(precio);
    }
}