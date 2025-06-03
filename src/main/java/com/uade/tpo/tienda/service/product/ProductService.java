package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.Producto;

import io.jsonwebtoken.io.IOException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  Page<Producto> getProducts(Pageable pageable);
  // Nuevo método para admin - obtiene todos los productos (activos e inactivos y bajo stock)
  Page<Producto> getAllProductsForAdmin(Pageable pageable);
 
 
  Producto updateProductWithImages(Producto producto);
  
  Producto updateStockProduct(Long id, Integer newStock);
  void deleteProduct(Long id);
  Producto getProductById(Long id);
  Page<Producto> filtrarProductosOrdenados(
  String nombre, Long categoriaId, 
  String tipoCuero, String grosor, 
  String acabado, String color, 
  Double precioMin, Double precioMax,
  String ordenarPor, String orden,
  Pageable pageable) ;
  Producto activarProducto(Long id) ;
  
  Producto saveProductWithImages(Producto producto) throws IOException;
  List<String> getTiposCueroDisponibles();
  List<String> getColoresDisponibles();
  void eliminarFotoDeProducto(Long productoId, Long fotoId);
  void agregarFotoAProducto(Long productoId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException, java.sql.SQLException;
}
