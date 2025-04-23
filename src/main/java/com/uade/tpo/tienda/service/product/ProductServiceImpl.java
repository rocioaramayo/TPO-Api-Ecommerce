package com.uade.tpo.tienda.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.ProductResponse;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.FaltanDatosException;
import com.uade.tpo.tienda.exceptions.ProductoInactivoException;
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
      // validar que no exista un producto igual (nombre + categoría)
      boolean productoExiste = productRepository.existsByNombreAndCategoria_Id(
          producto.getNombre(),
          producto.getCategoria().getId()
      );

      if (productoExiste) {
          throw new ProductoYaExisteException();
      }

      // validar que tenga al menos una foto (no nulo y no vacío)
      if (producto.getFotos() == null || producto.getFotos().isEmpty()) {
          throw new ProductoSinImagenesException();
      }

      // Setear referencia a las fotos
      producto.getFotos().forEach(foto -> foto.setProducto(producto));

      // activarlo por defecto
      producto.setActivo(true);

      return productRepository.save(producto);
  }

  @Override
  public Page<Producto> getProducts(Pageable pageable) {
      return productRepository.findByStockGreaterThanAndActivoTrue(0, pageable);
  }

  @Override
  public Producto updateProduct(Long id, Producto productoUpdated) {
      Optional<Producto> optionalProducto = productRepository.findById(id);
      
      if (optionalProducto.isEmpty()) {
          throw new ProductoNoEncontradoException();
      }
  
      Producto existing = optionalProducto.get();
  
      if (!existing.isActivo()) {
          throw new ProductoInactivoException(); // si el producto está inactivo, no se puede actualizar
      }
  
      // Validar campos obligatorios (sin exigir fotos vacías)
      if (productoUpdated.getNombre() == null || 
          productoUpdated.getDescripcion() == null ||
          productoUpdated.getPrecio() == null ||
          productoUpdated.getStock() == null ||
          productoUpdated.getCategoria() == null ||
          productoUpdated.getFotos() == null) {
          throw new FaltanDatosException();
      }
  
      existing.setNombre(productoUpdated.getNombre());
      existing.setDescripcion(productoUpdated.getDescripcion());
      existing.setPrecio(productoUpdated.getPrecio());
      existing.setStock(productoUpdated.getStock());
      existing.setCategoria(productoUpdated.getCategoria());
  
      // si se mandan nuevas fotos, se reemplazan
      if (!productoUpdated.getFotos().isEmpty()) {
          existing.getFotos().clear();
          existing.getFotos().addAll(productoUpdated.getFotos());
          existing.getFotos().forEach(foto -> foto.setProducto(existing));
      }
  
      return productRepository.save(existing);
  }
  @Override
public Producto activarProducto(Long id) {
    Optional<Producto> productoOpt = productRepository.findById(id);
    if (productoOpt.isEmpty()) {
        throw new ProductoNoEncontradoException();
    }

    Producto producto = productoOpt.get();
    producto.setActivo(true);
    return productRepository.save(producto);
}
  @Override
  public void deleteProduct(Long id) {
    Optional<Producto> productoOpt = productRepository.findById(id);
    if (!productoOpt.isPresent()) {
        throw new ProductoNoEncontradoException();
    }

    Producto producto = productoOpt.get();
    producto.setActivo(false);
    productRepository.save(producto);
  }
  @Override
public Producto getProductById(Long id) {
    Producto producto = productRepository.findById(id)
        .orElseThrow(ProductoNoEncontradoException::new);

    if (!producto.isActivo()) {
        throw new ProductoInactivoException();
    }

    return producto;
}
@Override
public List<Producto> filtrarProductos(String nombre, String categoria, Double precioMax) {
    List<Producto> productos = productRepository.findAll();

    return productos.stream()
        .filter(p -> p.isActivo()) //solo si esta activo 
        .filter(p -> (nombre == null || p.getNombre().toLowerCase().contains(nombre.toLowerCase())))
        .filter(p -> (categoria == null || p.getCategoria().getNombre().equalsIgnoreCase(categoria)))
        .filter(p -> (precioMax == null || p.getPrecio() <= precioMax))
        .filter(p -> p.getStock() > 0) // solo si tiene stock
        .toList();
}

@Override
public Producto updateStockProduct(Long id, Integer newStock) {
  //Encuentro el produto por ID
  Optional<Producto> optionalProduct = productRepository.findById(id);
  if (optionalProduct.isPresent()) {
    // lo saco de optional y lo guardo en existing
    Producto existing = optionalProduct.get();
    if (!existing.isActivo()) {
      throw new ProductoInactivoException();
  }

    // y le sumo el nuevo stock que voy a tener al producto.
    existing.setStock(existing.getStock() + newStock);
    
    //lo guardo y lo devuelvo
    return productRepository.save(existing);

  }else{
    throw new ProductoNoEncontradoException();
  }
}



}