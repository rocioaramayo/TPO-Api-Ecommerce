
package com.uade.tpo.tienda.service.entrega;

import com.uade.tpo.tienda.dto.*;
import java.util.List;

public interface EntregaService {
    
    // Métodos para MetodoEntrega
    MetodoEntregaResponse crearMetodoEntrega(MetodoEntregaRequest request);
    MetodoEntregaResponse actualizarMetodoEntrega(Long id, MetodoEntregaRequest request);
    void activarMetodoDeEntrega(Long id);
    void eliminarMetodoEntrega(Long id);
    List<MetodoEntregaResponse> obtenerMetodosEntrega();
    List<MetodoEntregaResponse> obtenerMetodosEntregaActivos();
    MetodoEntregaResponse obtenerMetodoEntregaPorId(Long id);
    
    // Métodos para PuntoRetiro
    PuntoRetiroResponse crearPuntoRetiro(PuntoRetiroRequest request);
    PuntoRetiroResponse actualizarPuntoRetiro(Long id, PuntoRetiroRequest request);
    void activarPuntoRetiro(Long id);
    void eliminarPuntoRetiro(Long id);
    List<PuntoRetiroResponse> obtenerPuntosRetiro();
    List<PuntoRetiroResponse> obtenerPuntosRetiroActivos();
    List<PuntoRetiroResponse> obtenerPuntosRetiroPorMetodo(Long metodoEntregaId);
    PuntoRetiroResponse obtenerPuntoRetiroPorId(Long id);
    CotizacionEnvioResponse cotizarEnvio(CotizacionEnvioRequest request);

}