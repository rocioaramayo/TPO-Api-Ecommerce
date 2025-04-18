package com.uade.tpo.tienda.service.compra;

import java.util.List;

import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.entity.Compra;

public interface InterfazCompraService {
    void procesarCompra(CompraRequest request);
List<Compra> obtenerComprasDeUsuario(String email);
List<Compra> obtenerTodas() ;
}
