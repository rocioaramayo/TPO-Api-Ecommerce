package com.uade.tpo.tienda.service.direccion;

import java.util.List;

import com.uade.tpo.tienda.dto.DireccionRequest;
import com.uade.tpo.tienda.dto.DireccionResponse;

public interface DireccionService {

    


 List<DireccionResponse> obtenerDireccionesDelUsuario(String email);
    void guardarDireccion(String email, DireccionRequest request);
    void desactivarDireccion(Long id);
    
}
