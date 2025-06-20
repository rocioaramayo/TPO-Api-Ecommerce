package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ProductService {

    Producto saveProductWithImages(Producto producto);

    Page<Producto> getAllProductsForAdmin(Pageable pageable);

    Producto updateProductWithImages(Producto producto);

    Page<Producto> getProducts(Pageable pageable);

    Producto activarProducto(Long id);
    
    void deleteProduct(Long id);

    Producto getProductById(Long id);

    List<String> getTiposCueroDisponibles();

    List<String> getColoresDisponibles();

    Page<Producto> filtrarProductosOrdenados(
        String nombre, Long categoriaId, String tipoCuero, String grosor, String acabado, 
        String color, Double precioMin, Double precioMax, String ordenarPor, String orden, Pageable pageable
    );

    Producto updateStockProduct(Long id, Integer newStock);

    void eliminarFotoDeProducto(Long productoId, Long fotoId);

    void agregarFotoAProducto(Long productoId, MultipartFile file) throws IOException, SQLException;
}
