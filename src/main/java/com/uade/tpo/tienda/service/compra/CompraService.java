package com.uade.tpo.tienda.service.compra;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
 
import com.uade.tpo.tienda.dto.CompraItemRequest;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.CompraItem;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
 
 
import jakarta.transaction.Transactional;
 
@Service
public class CompraService {
 
    @Autowired
    private ProductRepository productoRepository; // No inicializar con null
    @Autowired
    private CompraRepository compraRepository; // No inicializar con null
    @Autowired
    private UsuarioRepository usuarioRepository; // No inicializar con null
 
    @Transactional
    public void procesarCompra(CompraRequest request) {
        // Lista de ítems de compra (CompraItem)
        List<CompraItem> items = new ArrayList<>();
 
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
 
        Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
       
        // Iteramos sobre cada item de la compra en la request
        for (CompraItemRequest itemReq : request.getItems()) {
            // Buscamos el producto correspondiente
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
 
            // Comprobamos si hay suficiente stock
            if (producto.getStock() < itemReq.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
            }
 
            // Reducimos el stock del producto
            producto.setStock(producto.getStock() - itemReq.getCantidad());
            productoRepository.save(producto);
 
            // Creamos un CompraItem para el ítem de la compra
            CompraItem item = new CompraItem();
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
 
            // Asignamos el CompraItem a la lista de ítems
            items.add(item);
        }
 
        // Creamos una nueva compra
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setItems(items);
        compra.setUsuario(usuario); // Asignamos el usuario encontrado en la base de datos
 
        // Guardamos la compra en el repositorio
        compraRepository.save(compra);
    }
}