package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.CategoryRequest;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Endpoint para crear una categoría.
     * Recibe en el body un JSON con los campos 'nombre' y 'descripcion'.
     */
    @PostMapping("/create")
    public ResponseEntity<Categoria> createCategory(@RequestBody CategoryRequest request) {
        // Construir la entidad Categoria usando el builder de Lombok
        Categoria category = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        // Llamar al servicio para guardar la categoría
        Categoria created = categoryService.createCategory(category);

        // Retornar la respuesta 201 Created con la URI del recurso creado
        return ResponseEntity.created(URI.create("/categories/" + created.getId())).body(created);
    }
}