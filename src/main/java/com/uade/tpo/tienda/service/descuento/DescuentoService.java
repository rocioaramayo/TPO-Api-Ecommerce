package com.uade.tpo.tienda.service.descuento;

import com.uade.tpo.tienda.dto.*;

import java.util.List;

public interface DescuentoService {

    DescuentoResponse crearDescuento(DescuentoRequest request);
    DescuentoResponse actualizarDescuento(Long id, DescuentoRequest request);
    MessageResponse eliminarDescuento(Long id);
    List<DescuentoResponse> obtenerTodosDescuentos();
    DescuentoResponse cambiarEstadoDescuento(Long id, boolean activo);
    DescuentoAplicadoResponse validarDescuento(ValidarDescuentoRequest request);
}
