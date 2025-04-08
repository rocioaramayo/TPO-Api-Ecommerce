package com.uade.tpo.tienda.service.product;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.repository.ProductRepository;
@Service
public class ProductServiceImpl implements ProductService{
  @Autowired
  private ProductRepository productRepository;

  @Override
  public Producto createProduct(Producto producto) {
    if (producto.getFotos() != null) {
      producto.getFotos().forEach(foto -> foto.setProducto(producto));
  }
  return productRepository.save(producto);
  }

  @Override
  public Page<Producto> getProducts(Pageable pageable) {
    return productRepository.findByStockGreaterThan(0, pageable);
  }

  @Override
  public Producto updateProduct(Long id, Producto productoUpdated) {
    Optional<Producto> optionalProducto = productRepository.findById(id);
    if(optionalProducto.isPresent()){
        Producto existing = optionalProducto.get();
        // actualizamos los campos 
        existing.setNombre(productoUpdated.getNombre());
        existing.setDescripcion(productoUpdated.getDescripcion());
        existing.setPrecio(productoUpdated.getPrecio());
        existing.setStock(productoUpdated.getStock());
        existing.setCategoria(productoUpdated.getCategoria());
        existing.setVendedor(productoUpdated.getVendedor());
        // se puedes actualizar la lista 
        existing.setFotos(productoUpdated.getFotos());
        
        return productRepository.save(existing);
    }
    
    throw new RuntimeException("Producto no encontrado con id: " + id);
}
  @Override
  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }
  
}
