package com.uade.tpo.tienda.controllers;


import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.ProductDTO;
import com.uade.tpo.tienda.dto.ProductPageResponse;
import com.uade.tpo.tienda.dto.ProductResponse;
import com.uade.tpo.tienda.dto.StockRequest;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.entity.FotoProducto;
import com.uade.tpo.tienda.entity.Producto;

import com.uade.tpo.tienda.service.product.ProductService;
import com.uade.tpo.tienda.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@RestController
@RequestMapping("productos")
public class ProductController {
    @Autowired 
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;    
    // CREAR PRODUCTO - Exactamente como la profesora
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProductWithImages(@ModelAttribute ProductDTO productDTO,
        @RequestParam("files") MultipartFile[] files) {
        try {
            // Validar que la categoría existe
            Optional<Categoria> categoryOpt = categoryService.getCategoryById(productDTO.getCategoryId());
            if (!categoryOpt.isPresent()) {
                return new ResponseEntity<>("Categoría no encontrada", HttpStatus.BAD_REQUEST);
            }
            
            // Crear producto
            Producto producto = new Producto();
            producto.setNombre(productDTO.getNombre());
            producto.setDescripcion(productDTO.getDescripcion());
            producto.setPrecio(productDTO.getPrecio());
            producto.setStock(productDTO.getStock());
            producto.setCategoria(categoryOpt.get());
            producto.setTipoCuero(productDTO.getTipoCuero());
            producto.setGrosor(productDTO.getGrosor());
            producto.setAcabado(productDTO.getAcabado());
            producto.setColor(productDTO.getColor());
            producto.setTextura(productDTO.getTextura());
            producto.setInstruccionesCuidado(productDTO.getInstruccionesCuidado());
            producto.setActivo(productDTO.getActivo() != null ? productDTO.getActivo() : true);

            // Procesar archivos de imagen - EXACTAMENTE como la profesora
            List<FotoProducto> fotos = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    byte[] bytes = file.getBytes();
                    javax.sql.rowset.serial.SerialBlob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                    
                    FotoProducto foto = FotoProducto.builder()
                            .image(blob)
                            .producto(producto)
                            .build();
                    fotos.add(foto);
                }
            }
            
            producto.setFotos(fotos);
            productService.saveProductWithImages(producto);
            
            return new ResponseEntity<>("Producto subido exitosamente", HttpStatus.OK);
        } catch (IOException | SQLException e) {
            return new ResponseEntity<>("Error al subir producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ACTUALIZAR PRODUCTO - Nuevo método que funciona con el sistema actual
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProductWithImages(
            @PathVariable Long id,
            @ModelAttribute ProductDTO productDTO,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "fotosAEliminar", required = false) List<Long> fotosAEliminar) {
        try {
            // Validar que la categoría existe
            Optional<Categoria> categoryOpt = categoryService.getCategoryById(productDTO.getCategoryId());
            if (!categoryOpt.isPresent()) {
                return new ResponseEntity<>("Categoría no encontrada", HttpStatus.BAD_REQUEST);
            }
            
            // Buscar producto existente
            Producto productoExistente = productService.getProductById(id);
            
            // Eliminar fotos marcadas para eliminar
            if (fotosAEliminar != null && !fotosAEliminar.isEmpty()) {
                productoExistente.getFotos().removeIf(foto -> fotosAEliminar.contains(foto.getId()));
            }
            
            // Actualizar campos básicos
            productoExistente.setNombre(productDTO.getNombre());
            productoExistente.setDescripcion(productDTO.getDescripcion());
            productoExistente.setPrecio(productDTO.getPrecio());
            productoExistente.setStock(productDTO.getStock());
            productoExistente.setCategoria(categoryOpt.get());
            productoExistente.setTipoCuero(productDTO.getTipoCuero());
            productoExistente.setGrosor(productDTO.getGrosor());
            productoExistente.setAcabado(productDTO.getAcabado());
            productoExistente.setColor(productDTO.getColor());
            productoExistente.setTextura(productDTO.getTextura());
            productoExistente.setInstruccionesCuidado(productDTO.getInstruccionesCuidado());
            boolean activo = productDTO.getActivo() == null ? true : productDTO.getActivo();
            productoExistente.setActivo(activo);

            // Si se envían nuevas imágenes, reemplazar las existentes
            if (files != null && files.length > 0) {
                // Limpiar imágenes existentes
                productoExistente.getFotos().clear();

                // Agregar nuevas imágenes a la MISMA colección
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        byte[] bytes = file.getBytes();
                        javax.sql.rowset.serial.SerialBlob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

                        FotoProducto foto = FotoProducto.builder()
                                .image(blob)
                                .producto(productoExistente)
                                .build();
                        productoExistente.getFotos().add(foto);
                    }
                }
            }
            // Si no se envían archivos, mantener las imágenes existentes
            
            // Actualizar producto
            productService.updateProductWithImages(productoExistente);
            
            return new ResponseEntity<>("Producto actualizado exitosamente", HttpStatus.OK);
            
        } catch (IOException | SQLException e) {
            return new ResponseEntity<>("Error al actualizar producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        @GetMapping("/tipos-cuero")
        public ResponseEntity<List<String>> getTiposCuero() {
            List<String> tiposCuero = productService.getTiposCueroDisponibles();
            return ResponseEntity.ok(tiposCuero);
        }

        @GetMapping("/colores")
        public ResponseEntity<List<String>> getColores() {
            List<String> colores = productService.getColoresDisponibles();
            return ResponseEntity.ok(colores);
        }
    // NUEVO ENDPOINT SOLO PARA ADMIN
    @GetMapping("/admin")
    public ResponseEntity<ProductPageResponse> getProductsForAdmin(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size) {

        Page<Producto> productos;
        if (page == null || size == null) {
            productos = productService.getAllProductsForAdmin(PageRequest.of(0, Integer.MAX_VALUE));
        } else {
            productos = productService.getAllProductsForAdmin(PageRequest.of(page, size));
        }

        List<ProductResponse> responseList = productos
            .stream()
            .map(this::mapToProductResponseForAdmin)
            .toList();

        ProductPageResponse response = ProductPageResponse.builder()
            .productos(responseList)
            .totalProductos(productos.getTotalElements())
            .paginaActual(productos.getNumber())
            .tamañoPagina(productos.getSize())
            .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable Long id) {
        Producto producto = productService.getProductById(id);
        return ResponseEntity.ok(mapToProductResponse(producto));
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size) {

        Page<Producto> productos;
        if (page == null || size == null) {
            productos = productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE));
        } else {
            productos = productService.getProducts(PageRequest.of(page, size));
        }

        List<ProductResponse> responseList = productos
            .stream()
            .map(this::mapToProductResponse)
            .toList();

        ProductPageResponse response = ProductPageResponse.builder()
            .productos(responseList)
            .totalProductos(productos.getTotalElements())
            .paginaActual(productos.getNumber())
            .tamañoPagina(productos.getSize())
            .build();

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/activar/{id}")
    public ResponseEntity<ProductResponse> activarProducto(@PathVariable Long id) {
        Producto activado = productService.activarProducto(id);
        return ResponseEntity.ok(mapToProductResponse(activado));
    }
    
    @PutMapping("stock/{id}")
    public ResponseEntity<ProductResponse> updateStockProduct (@PathVariable Long id, @RequestBody StockRequest stockRequest) {
        Producto updated = productService.updateStockProduct(id, stockRequest.getStock());
        return ResponseEntity.ok(mapToProductResponse(updated));
    }

    // MÉTODO HELPER - Convierte Blob a Base64 para mostrar en frontend
    private ProductResponse mapToProductResponse(Producto producto) {
        List<PhotoResponse> photoResponses = null;
        if (producto.getFotos() != null) {
            photoResponses = producto.getFotos().stream()
                .map(foto -> {
                    try {
                        String encodedString = Base64.getEncoder()
                            .encodeToString(foto.getImage().getBytes(1, (int) foto.getImage().length()));
                        return PhotoResponse.builder()
                            .id(foto.getId())
                            .file(encodedString)
                            .build();
                    } catch (SQLException e) {
                        throw new RuntimeException("Error procesando imagen", e);
                    }
                })
                .collect(Collectors.toList());
        }
        
        return ProductResponse.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .descripcion(producto.getDescripcion())
            .precio(producto.getPrecio())
            .stock(producto.getStock())
            .categoria(producto.getCategoria().getNombre())  
            .categoriaId(producto.getCategoria().getId())
            .fotos(photoResponses)
            .createdAt(producto.getCreatedAt())
            .pocoStock(producto.getStock() < 5)
            .tipoCuero(producto.getTipoCuero())
            .grosor(producto.getGrosor())
            .acabado(producto.getAcabado())
            .color(producto.getColor())
            .textura(producto.getTextura())
            .instruccionesCuidado(producto.getInstruccionesCuidado())
            .build();
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<ProductResponse>> filtrarProductos(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(required = false) String tipoCuero,
        @RequestParam(required = false) String grosor,
        @RequestParam(required = false) String acabado,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) Double precioMin,
        @RequestParam(required = false) Double precioMax,
        @RequestParam(required = false, defaultValue = "precio") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String orden,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "20") int size,
        @RequestParam(required = false) Long excludeId) {
        
        Page<Producto> productosPage = productService.filtrarProductosOrdenados(
            nombre, categoriaId, tipoCuero, grosor, acabado, color, precioMin, precioMax,
            ordenarPor, orden, PageRequest.of(page, size));
        
        // Si se especifica excludeId, filtrar el producto de la lista
        if (excludeId != null) {
            List<Producto> productosFiltrados = productosPage.getContent().stream()
                .filter(producto -> !producto.getId().equals(excludeId))
                .collect(Collectors.toList());
            
            // Crear una nueva página con los productos filtrados
            Page<Producto> productosFiltradosPage = new PageImpl<>(
                productosFiltrados, 
                productosPage.getPageable(), 
                productosPage.getTotalElements() - 1
            );
            
            Page<ProductResponse> responsePage = productosFiltradosPage.map(this::mapToProductResponse);
            return ResponseEntity.ok(responsePage);
        }
        
        Page<ProductResponse> responsePage = productosPage.map(this::mapToProductResponse);
        
        return ResponseEntity.ok(responsePage);
    }


    // MÉTODO HELPER PARA ADMIN - Incluye información adicional como estado activo/inactivo
    private ProductResponse mapToProductResponseForAdmin(Producto producto) {
        List<PhotoResponse> photoResponses = null;
        if (producto.getFotos() != null) {
            photoResponses = producto.getFotos().stream()
                .map(foto -> {
                    try {
                        String encodedString = Base64.getEncoder()
                            .encodeToString(foto.getImage().getBytes(1, (int) foto.getImage().length()));
                        return PhotoResponse.builder()
                            .id(foto.getId())
                            .file(encodedString)
                            .build();
                    } catch (SQLException e) {
                        throw new RuntimeException("Error procesando imagen", e);
                    }
                })
                .collect(Collectors.toList());
        }
        
        return ProductResponse.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .descripcion(producto.getDescripcion())
            .precio(producto.getPrecio())
            .stock(producto.getStock())
            .categoria(producto.getCategoria().getNombre())  
            .categoriaId(producto.getCategoria().getId())
            .fotos(photoResponses)
            .createdAt(producto.getCreatedAt())
            .pocoStock(producto.getStock() < 5)
            .tipoCuero(producto.getTipoCuero())
            .grosor(producto.getGrosor())
            .acabado(producto.getAcabado())
            .color(producto.getColor())
            .textura(producto.getTextura())
            .instruccionesCuidado(producto.getInstruccionesCuidado())
            .activo(producto.isActivo()) // Información adicional para admin
            .build();
    }

    // Endpoint para eliminar una foto específica de un producto
    @DeleteMapping("/{productoId}/fotos/{fotoId}")
    public ResponseEntity<Void> eliminarFotoDeProducto(
            @PathVariable Long productoId,
            @PathVariable Long fotoId) {
        productService.eliminarFotoDeProducto(productoId, fotoId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para agregar una foto a un producto existente
    @PostMapping("/{productoId}/fotos")
    public ResponseEntity<Void> agregarFotoAProducto(
            @PathVariable Long productoId,
            @RequestParam("file") MultipartFile file) {
        try {
            productService.agregarFotoAProducto(productoId, file);
            return ResponseEntity.ok().build();
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}