package com.uade.tpo.tienda.controllers;


import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.service.product.ProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;





@RequestMapping("productos")
@RestController
public class ProductController {
    @Autowired 
    private ProductService productService;
//crear
    @PostMapping
    public ResponseEntity<Producto> createProduct(@RequestBody Producto producto) {
        Producto created = productService.createProduct(producto);
        return ResponseEntity.created(URI.create("/productos/" + created.getId())).body(created);
    }



//listar productos
    @GetMapping
    public ResponseEntity<Page<Producto>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            // si alguno es null doy todos los productos
    
            return ResponseEntity.ok(productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE)));
        }
        //si se espefica
        return ResponseEntity.ok(productService.getProducts(PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteproduct (@PathVariable Long id){
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity <Producto> updateProduct(@PathVariable Long id, @RequestBody Producto producto) {
        Producto updated = productService.updateProducto(id, producto);
        return ResponseEntity.ok(updated);

    }
}
