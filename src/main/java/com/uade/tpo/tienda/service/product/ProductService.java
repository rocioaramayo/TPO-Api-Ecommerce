package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.Producto;

import io.jsonwebtoken.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  Page<Producto> getProducts(Pageable pageable);
  
 
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

}
