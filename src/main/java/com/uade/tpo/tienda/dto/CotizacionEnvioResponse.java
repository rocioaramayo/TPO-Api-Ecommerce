package com.uade.tpo.tienda.dto;

public class CotizacionEnvioResponse {
    private Double precio;

    public CotizacionEnvioResponse() {
    }

    public CotizacionEnvioResponse(Double precio) {
        this.precio = precio;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
