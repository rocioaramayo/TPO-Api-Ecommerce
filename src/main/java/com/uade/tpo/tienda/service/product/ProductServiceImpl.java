package com.uade.tpo.tienda.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.FaltanDatosException;
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

    if (producto.getFotos() != null || !producto.getFotos().isEmpty()) {
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
    if (optionalProducto.isEmpty()) {
      throw new ProductoNoEncontradoException();
    }

  // aca voy a validar si tengo todos los datos que necesito para actualizar
    if (productoUpdated.getNombre() == null || 
      productoUpdated.getDescripcion() == null ||
      productoUpdated.getPrecio() == null ||
      productoUpdated.getStock() == null ||
      productoUpdated.getCategoria() == null ||
      productoUpdated.getFotos() == null || productoUpdated.getFotos().isEmpty()) throw new FaltanDatosException();

    Producto existing = optionalProducto.get();

    existing.setNombre(productoUpdated.getNombre());
    existing.setDescripcion(productoUpdated.getDescripcion());
    existing.setPrecio(productoUpdated.getPrecio());
    existing.setStock(productoUpdated.getStock());
    existing.setCategoria(productoUpdated.getCategoria());
    existing.getFotos().clear();
    existing.getFotos().addAll(productoUpdated.getFotos());
    existing.getFotos().forEach(foto -> {
      foto.setProducto(existing);
    });
    return productRepository.save(existing);
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

@Override
public Producto updateStockProduct(Long id, Integer newStock) {
  //Encuentro el produto por ID
  Optional<Producto> optionalProduct = productRepository.findById(id);
  if (optionalProduct.isPresent()) {
    // lo saco de optional y lo guardo en existing
    Producto existing = optionalProduct.get();
    
    // y le sumo el nuevo stock que voy a tener al producto.
    existing.setStock(existing.getStock() + newStock);
    
    //lo guardo y lo devuelvo
    return productRepository.save(existing);

  }else{
    throw new ProductoNoEncontradoException();
  }
}


}