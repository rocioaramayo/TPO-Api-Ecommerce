package com.uade.tpo.tienda.controllers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.ProductRequest;
import com.uade.tpo.tienda.dto.ProductResponse;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.entity.FotoProducto;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.category.CategoryService;
import com.uade.tpo.tienda.service.product.ProductService;
import com.uade.tpo.tienda.service.usuario.UsuarioService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("productos")
public class ProductController {
  @Autowired 
  private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UsuarioService usuarioService;
  // crear producto
  @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        //validar que la categoría existe
        Optional<Categoria> categoryOpt = categoryService.getCategoryById(request.getCategoryId());
        if (!categoryOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Categoria categoria = categoryOpt.get();
        
        // validar que el vendedor (usuario) existe
        Optional<Usuario> vendorOpt = usuarioService.obtenerUsuarioPorId(request.getVendedorId());
        if (!vendorOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Usuario vendedor = vendorOpt.get();
        
        // transformar la lista de PhotoRequest en entidad FotoProducto
        List<FotoProducto> fotos = null;
        if (request.getFotos() != null) {
            fotos = request.getFotos().stream().map(photoReq ->
                FotoProducto.builder()
                    .url(photoReq.getUrl())
                    .descripcion(photoReq.getDescripcion())
                    .build()
            ).collect(Collectors.toList());
        }
        
        // construir Producto 
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(categoria)
                .vendedor(vendedor)
                .fotos(fotos)   
                .build();
                
        // 5. Persistir el producto 
        Producto created = productService.createProduct(producto);
        // 6. Mapear el producto creado a un DTO de salida
        ProductResponse response = mapToProductResponse(created);
        return ResponseEntity.created(URI.create("/productos/" + created.getId())).body(response);
    }


  // listar productos
  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getProducts(
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer size) {
      Page<Producto> productos;
      if (page == null || size == null) {
          productos = productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE));
      } else {
          productos = productService.getProducts(PageRequest.of(page, size));
      }
      
      // mapeo Producto a ProductResponse
      Page<ProductResponse> responsePage = productos.map(this::mapToProductResponse);
      return ResponseEntity.ok(responsePage);
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();

  }

  @PutMapping("/{id}")
  public ResponseEntity<Producto> updateProduct(@PathVariable Long id, @RequestBody Producto producto) {
    Producto updated= productService.updateProduct(id,producto);
    return ResponseEntity.ok(updated);
  }

  private ProductResponse mapToProductResponse(Producto producto) {
    // Mapear las fotos
    List<PhotoResponse> photoResponses = null;
    if (producto.getFotos() != null) {
        photoResponses = producto.getFotos().stream()
            .map(foto -> PhotoResponse.builder()
                .id(foto.getId())
                .url(foto.getUrl())
                .descripcion(foto.getDescripcion())
                .build())
            .collect(Collectors.toList());
    }
    
    return ProductResponse.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .descripcion(producto.getDescripcion())
            .precio(producto.getPrecio())
            .stock(producto.getStock())
            .categoria(producto.getCategoria().getNombre())  
            .vendedor(producto.getVendedor().getUsername())   
            .fotos(photoResponses)
            .createdAt(producto.getCreatedAt())
            .build();
}

}
