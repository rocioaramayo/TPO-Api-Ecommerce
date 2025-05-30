
package com.uade.tpo.tienda.service.entrega;

import com.uade.tpo.tienda.dto.*;
import java.util.List;

public interface EntregaService {
    
    // Métodos para MetodoEntrega
    MetodoEntregaResponse crearMetodoEntrega(MetodoEntregaRequest request);
    MetodoEntregaResponse actualizarMetodoEntrega(Long id, MetodoEntregaRequest request);
    void eliminarMetodoEntrega(Long id);
    List<MetodoEntregaResponse> obtenerMetodosEntrega();
    List<MetodoEntregaResponse> obtenerMetodosEntregaActivos();
    MetodoEntregaResponse obtenerMetodoEntregaPorId(Long id);
    
    // Métodos para PuntoRetiro
    PuntoRetiroResponse crearPuntoRetiro(PuntoRetiroRequest request);
    PuntoRetiroResponse actualizarPuntoRetiro(Long id, PuntoRetiroRequest request);
    void eliminarPuntoRetiro(Long id);
    List<PuntoRetiroResponse> obtenerPuntosRetiro();
    List<PuntoRetiroResponse> obtenerPuntosRetiroActivos();
    List<PuntoRetiroResponse> obtenerPuntosRetiroPorMetodo(Long metodoEntregaId);
    PuntoRetiroResponse obtenerPuntoRetiroPorId(Long id);
    CotizacionEnvioResponse cotizarEnvio(CotizacionEnvioRequest request);
    void guardarMetodoDelUsuario(String email, Long metodoEntregaId);
    List<MetodoEntregaResponse> obtenerMetodosDelUsuario(String email);

}