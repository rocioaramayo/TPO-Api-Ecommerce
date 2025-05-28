package com.uade.tpo.tienda.service.image;

import com.uade.tpo.tienda.entity.FotoProducto;
import org.springframework.stereotype.Service;

@Service
public interface ImageService {
    public FotoProducto create(FotoProducto foto);
    public FotoProducto viewById(long id);
}