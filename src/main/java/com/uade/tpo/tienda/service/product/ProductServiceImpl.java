package com.uade.tpo.tienda.service.product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
// Imports adicionales para ProductController.java

// Imports adicionales para ProductServiceImpl.java
import java.util.Comparator;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.FaltanDatosException;
import com.uade.tpo.tienda.exceptions.ImagenDemasiadoGrandeException;
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
  
      // Validar tamaño de cada imagen
      producto.getFotos().forEach(foto -> {
          validarImagenBase64(foto.getContenidoBase64());
      });
  
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
        throw new ProductoInactivoException();
    }

    // Validar campos obligatorios
    if (productoUpdated.getNombre() == null || 
        productoUpdated.getDescripcion() == null ||
        productoUpdated.getPrecio() == null ||
        productoUpdated.getStock() == null ||
        productoUpdated.getCategoria() == null) {
        throw new FaltanDatosException();
    }

    // Si se proporcionan nuevas fotos, valida su tamaño
    if (productoUpdated.getFotos() != null && !productoUpdated.getFotos().isEmpty()) {
        productoUpdated.getFotos().forEach(foto -> {
            validarImagenBase64(foto.getContenidoBase64());
        });
    }

    // Actualizar los campos
    existing.setNombre(productoUpdated.getNombre());
    existing.setDescripcion(productoUpdated.getDescripcion());
    existing.setPrecio(productoUpdated.getPrecio());
    existing.setStock(productoUpdated.getStock());
    existing.setCategoria(productoUpdated.getCategoria());

    // si se proporcionan nuevas fotos, reemplazar las existentes
    if (productoUpdated.getFotos() != null && !productoUpdated.getFotos().isEmpty()) {
        existing.getFotos().clear();
        existing.getFotos().addAll(productoUpdated.getFotos());
        existing.getFotos().forEach(foto -> foto.setProducto(existing));
    }

    return productRepository.save(existing);
}
  public void validarImagenBase64(String contenidoBase64) {
    if (contenidoBase64 == null || contenidoBase64.isEmpty()) {
        throw new IllegalArgumentException("La imagen no puede estar vacía");
    }
    
    // calcular tamaño aproximado en bytes
    int tamanoAprox = contenidoBase64.length() * 3 / 4; // aproximación del tamaño en bytes
    
    // establecer un límite, por ejemplo 2MB
    final int LIMITE_TAMANO = 2 * 1024 * 1024; // 2MB
    
    if (tamanoAprox > LIMITE_TAMANO) {
        throw new ImagenDemasiadoGrandeException();
    }
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
public Page<Producto> filtrarProductosOrdenados(
    String nombre, Long categoriaId, 
    String tipoCuero, String grosor, 
    String acabado, String color, 
    Double precioMin, Double precioMax,
    String ordenarPor, String orden,
    Pageable pageable) {
    
    // Primero obtenemos todos los productos
    List<Producto> todosProductos = productRepository.findAll();
    
    // Aplicamos los filtros
    List<Producto> productosFiltrados = todosProductos.stream()
        .filter(p -> p.isActivo()) // Solo productos activos
        .filter(p -> p.getStock() > 0) // Solo productos con stock
        
        // Filtrar por nombre (si se proporciona)
        .filter(p -> nombre == null || 
                     p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
        
        // Filtrar por categoría (si se proporciona)
        .filter(p -> categoriaId == null || 
                    (p.getCategoria() != null && 
                     p.getCategoria().getId().equals(categoriaId)))
        
        // Filtrar por tipoCuero (si se proporciona)
        .filter(p -> tipoCuero == null || 
                    (p.getTipoCuero() != null && 
                     p.getTipoCuero().equalsIgnoreCase(tipoCuero)))
        
        // Filtrar por grosor (si se proporciona)
        .filter(p -> grosor == null || 
                    (p.getGrosor() != null && 
                     p.getGrosor().equalsIgnoreCase(grosor)))
        
        // Filtrar por acabado (si se proporciona)
        .filter(p -> acabado == null || 
                    (p.getAcabado() != null && 
                     p.getAcabado().equalsIgnoreCase(acabado)))
        
        // Filtrar por color (si se proporciona)
        .filter(p -> color == null || 
                    (p.getColor() != null && 
                     p.getColor().equalsIgnoreCase(color)))
        
        // Filtrar por precio mínimo (si se proporciona)
        .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
        
        // Filtrar por precio máximo (si se proporciona)
        .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
        
        .collect(Collectors.toList());
    
    // Aplicar ordenamiento
    Comparator<Producto> comparator;
    switch (ordenarPor.toLowerCase()) {
        case "nombre":
            comparator = Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER);
            break;
        case "precio":
            comparator = Comparator.comparing(Producto::getPrecio);
            break;
        case "fecha":
            comparator = Comparator.comparing(Producto::getCreatedAt);
            break;
        case "stock":
            comparator = Comparator.comparing(Producto::getStock);
            break;
        default:
            comparator = Comparator.comparing(Producto::getPrecio);
    }
    
    // Invertir el orden si es descendente
    if ("desc".equalsIgnoreCase(orden)) {
        comparator = comparator.reversed();
    }
    
    // Ordenar la lista
    List<Producto> productosOrdenados = productosFiltrados.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    
    // Aplicar paginación
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productosOrdenados.size());
    
    // Si el inicio es mayor que el tamaño de la lista, ajustamos para evitar errores
    if (start > productosOrdenados.size()) {
        start = 0;
        end = Math.min(pageable.getPageSize(), productosOrdenados.size());
    }
    
    // Crear subconjunto paginado
    List<Producto> contenidoPaginado = productosOrdenados.subList(start, end);
    
    // Crear objeto Page con metadatos de paginación
    return new PageImpl<>(
        contenidoPaginado, 
        pageable, 
        productosOrdenados.size()
    );
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