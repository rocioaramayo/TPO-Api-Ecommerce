package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  Producto createProduct(Producto product);
  Page<Producto> getProducts(Pageable pageable);
  Producto updateProduct(Long id, Producto productoUpdated);
  void deleteProduct(Long id);
}
