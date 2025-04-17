package com.uade.tpo.tienda.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.ProductoSinImagenesException;
import com.uade.tpo.tienda.exceptions.ProductoYaExisteException;
import com.uade.tpo.tienda.repository.ProductRepository;
@Service
public class ProductServiceImpl implements ProductService{
  @Autowired
  private ProductRepository productRepository;

  @Override
  public Producto createProduct(Producto producto) {
    // Validar que no exista un producto igual (nombre + categoría)
    boolean productoExiste = productRepository.existsByNombreAndCategoria_Id(
        producto.getNombre(),
        producto.getCategoria().getId()
    );

    if (productoExiste) {
        throw new ProductoYaExisteException(); // creás esta excepción
    }

    if (producto.getFotos() != null) {
      producto.getFotos().forEach(foto -> foto.setProducto(producto));
      return productRepository.save(producto);
  }// agregar si no sube con fotos no dejar 
    throw new ProductoSinImagenesException();

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
        // se puedes actualizar la lista 
        existing.setFotos(productoUpdated.getFotos());
        
        return productRepository.save(existing);
    }
    
    throw new ProductoNoEncontradoException();

}
  @Override
  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }
  @Override
  public Producto getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(ProductoNoEncontradoException::new);
}
@Override
public List<Producto> filtrarProductos(String nombre, String categoria, Double precioMax) {
    List<Producto> productos = productRepository.findAll();

    return productos.stream()
        .filter(p -> (nombre == null || p.getNombre().toLowerCase().contains(nombre.toLowerCase())))
        .filter(p -> (categoria == null || p.getCategoria().getNombre().equalsIgnoreCase(categoria)))
        .filter(p -> (precioMax == null || p.getPrecio() <= precioMax))
        .filter(p -> p.getStock() > 0) // solo si tiene  stock
        .toList();
}


}