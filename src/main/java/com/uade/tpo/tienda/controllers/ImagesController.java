package com.uade.tpo.tienda.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.dto.AddFileRequest;
import com.uade.tpo.tienda.dto.ImageResponse;
import com.uade.tpo.tienda.entity.FotoProducto;
import com.uade.tpo.tienda.service.image.ImageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("images")
@CrossOrigin(origins = "http://localhost:5173")
public class ImagesController {
    
    @Autowired
    private ImageService imageService;

    @GetMapping()
    public ResponseEntity<ImageResponse> displayImage(@RequestParam("id") long id) throws IOException, SQLException {
        FotoProducto foto = imageService.viewById(id);
        
        // Convertir Blob a Base64
        String encodedString = Base64.getEncoder()
                .encodeToString(foto.getImage().getBytes(1, (int) foto.getImage().length()));
        
        // Crear respuesta - SOLO con los campos que existen
        ImageResponse response = new ImageResponse();
        response.setId(foto.getId());
        response.setFile(encodedString);
                
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<String> addImagePost(AddFileRequest request) throws IOException, SerialException, SQLException {
        // Validar que el archivo no esté vacío
        if (request.getFile().isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }
        
        // Convertir archivo a Blob
        byte[] bytes = request.getFile().getBytes();
        javax.sql.rowset.serial.SerialBlob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
        
        // Crear FotoProducto
        FotoProducto foto = FotoProducto.builder()
                .image(blob)
                .build();
                
        imageService.create(foto);
        
        return ResponseEntity.ok("created");
    }
}