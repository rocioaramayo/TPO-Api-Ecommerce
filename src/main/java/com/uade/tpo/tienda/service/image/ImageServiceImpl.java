package com.uade.tpo.tienda.service.image;

import com.uade.tpo.tienda.entity.FotoProducto;
import com.uade.tpo.tienda.repository.FotoProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    
    @Autowired
    private FotoProductoRepository fotoProductoRepository;

    @Override
    public FotoProducto create(FotoProducto foto) {
        return fotoProductoRepository.save(foto);
    }

    @Override
    public FotoProducto viewById(long id) {
        return fotoProductoRepository.findById(id).get();
    }
}