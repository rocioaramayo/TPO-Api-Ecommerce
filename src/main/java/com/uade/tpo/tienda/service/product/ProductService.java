package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.Producto;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  Producto createProduct(Producto product);
  Page<Producto> getProducts(Pageable pageable);
  Producto updateProduct(Long id, Producto productoUpdated);
  Producto updateStockProduct(Long id, Integer newStock);
  void deleteProduct(Long id);
  Producto getProductById(Long id);
  List<Producto> filtrarProductos(String nombre, String categoria, Double precioMax);
  Producto activarProducto(Long id) ;
  void validarImagenBase64(String contenidoBase64);

}
