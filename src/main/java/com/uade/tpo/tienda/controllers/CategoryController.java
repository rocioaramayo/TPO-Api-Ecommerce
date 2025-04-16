package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.CategoryRequest;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.service.category.CategoryService;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;




@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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

    @GetMapping
    public ResponseEntity<List<Categoria>> getCategory() {
        List<Categoria> categories= categoryService.getCategory();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoryById(@PathVariable Long id) {
       Optional <Categoria> categoria =categoryService.getCategoryById(id);
       return categoria.map(ResponseEntity:: ok)
       .orElse(ResponseEntity.notFound().build());
    }
    


}