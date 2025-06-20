package com.uade.tpo.tienda.service.product;

import com.uade.tpo.tienda.entity.FotoProducto;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.exceptions.FaltanDatosException;
import com.uade.tpo.tienda.exceptions.ProductoInactivoException;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.ProductoSinImagenesException;
import com.uade.tpo.tienda.exceptions.ProductoYaExisteException;
import com.uade.tpo.tienda.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Producto saveProductWithImages(Producto producto) {
        boolean productoExiste = productRepository.existsByNombreAndCategoria_Id(producto.getNombre(), producto.getCategoria().getId());
        if (productoExiste) throw new ProductoYaExisteException();
        if (producto.getFotos() == null || producto.getFotos().isEmpty()) throw new ProductoSinImagenesException();
        producto.getFotos().forEach(foto -> foto.setProducto(producto));
        producto.setActivo(producto.getActivo());
        return productRepository.save(producto);
    }

    @Override
    public Page<Producto> getAllProductsForAdmin(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Producto updateProductWithImages(Producto producto) {
        if (producto.getId() == null) throw new ProductoNoEncontradoException();
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty() ||
            producto.getDescripcion() == null || producto.getDescripcion().trim().isEmpty() ||
            producto.getPrecio() == null || producto.getPrecio() <= 0 ||
            producto.getStock() == null || producto.getStock() < 0 ||
            producto.getCategoria() == null) throw new FaltanDatosException();
        
        if (producto.getFotos() != null && !producto.getFotos().isEmpty()) {
            for (FotoProducto foto : producto.getFotos()) {
                if (foto.getImage() == null) throw new ProductoSinImagenesException();
            }
            producto.getFotos().forEach(foto -> foto.setProducto(producto));
        }
        return productRepository.save(producto);
    }

    @Override
    public Page<Producto> getProducts(Pageable pageable) {
        return productRepository.findByStockGreaterThanAndActivoTrue(0, pageable);
    }

    @Override
    public Producto activarProducto(Long id) {
        Producto producto = productRepository.findById(id).orElseThrow(ProductoNoEncontradoException::new);
        producto.setActivo(true);
        return productRepository.save(producto);
    }

    @Override
    public void deleteProduct(Long id) {
        Producto producto = productRepository.findById(id).orElseThrow(ProductoNoEncontradoException::new);
        producto.setActivo(false);
        productRepository.save(producto);
    }

    @Override
    public Producto getProductById(Long id) {
        Producto producto = productRepository.findById(id).orElseThrow(ProductoNoEncontradoException::new);
        if (!producto.isActivo()) throw new ProductoInactivoException();
        return producto;
    }

    @Override
    public List<String> getTiposCueroDisponibles() {
        return productRepository.findAll().stream()
            .filter(p -> p.isActivo() && p.getTipoCuero() != null && !p.getTipoCuero().trim().isEmpty())
            .map(Producto::getTipoCuero).distinct().sorted().collect(Collectors.toList());
    }

    @Override
    public List<String> getColoresDisponibles() {
        return productRepository.findAll().stream()
            .filter(p -> p.isActivo() && p.getColor() != null && !p.getColor().trim().isEmpty())
            .map(Producto::getColor).distinct().sorted().collect(Collectors.toList());
    }

    @Override
    public Page<Producto> filtrarProductosOrdenados(String nombre, Long categoriaId, String tipoCuero, String grosor, String acabado, String color, Double precioMin, Double precioMax, String ordenarPor, String orden, Pageable pageable) {
        List<Producto> productosFiltrados = productRepository.findAll().stream()
            .filter(Producto::isActivo)
            .filter(p -> p.getStock() > 0)
            .filter(p -> nombre == null || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .filter(p -> categoriaId == null || (p.getCategoria() != null && p.getCategoria().getId().equals(categoriaId)))
            .filter(p -> tipoCuero == null || (p.getTipoCuero() != null && p.getTipoCuero().equalsIgnoreCase(tipoCuero)))
            .filter(p -> grosor == null || (p.getGrosor() != null && p.getGrosor().equalsIgnoreCase(grosor)))
            .filter(p -> acabado == null || (p.getAcabado() != null && p.getAcabado().equalsIgnoreCase(acabado)))
            .filter(p -> color == null || (p.getColor() != null && p.getColor().equalsIgnoreCase(color)))
            .filter(p -> precioMin == null || p.getPrecio() >= precioMin)
            .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
            .collect(Collectors.toList());

        Comparator<Producto> comparator;
        switch (ordenarPor.toLowerCase()) {
            case "nombre": comparator = Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER); break;
            case "precio": comparator = Comparator.comparing(Producto::getPrecio); break;
            case "fecha": comparator = Comparator.comparing(Producto::getCreatedAt); break;
            case "stock": comparator = Comparator.comparing(Producto::getStock); break;
            default: comparator = Comparator.comparing(Producto::getPrecio);
        }

        if ("desc".equalsIgnoreCase(orden)) {
            comparator = comparator.reversed();
        }

        List<Producto> productosOrdenados = productosFiltrados.stream().sorted(comparator).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productosOrdenados.size());
        if (start > productosOrdenados.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), productosOrdenados.size());
        }
        
        List<Producto> pageContent = productosOrdenados.subList(start, end);
        return new PageImpl<>(pageContent, pageable, productosOrdenados.size());
    }

    @Override
    public Producto updateStockProduct(Long id, Integer newStock) {
        Producto producto = productRepository.findById(id).orElseThrow(ProductoNoEncontradoException::new);
        if (producto.getStock() + newStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        producto.setStock(producto.getStock() + newStock);
        return productRepository.save(producto);
    }

    @Override
    public void eliminarFotoDeProducto(Long productoId, Long fotoId) {
        Producto producto = productRepository.findById(productoId).orElseThrow(ProductoNoEncontradoException::new);
        producto.getFotos().removeIf(foto -> foto.getId().equals(fotoId));
        productRepository.save(producto);
    }

    @Override
    public void agregarFotoAProducto(Long productoId, MultipartFile file) throws IOException, SQLException {
        Producto producto = productRepository.findById(productoId).orElseThrow(ProductoNoEncontradoException::new);
        byte[] bytes = file.getBytes();
        javax.sql.rowset.serial.SerialBlob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
        FotoProducto foto = FotoProducto.builder().image(blob).producto(producto).build();
        producto.getFotos().add(foto);
        productRepository.save(producto);
    }
}