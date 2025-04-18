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
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.StockInsuficienteException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
 
 
import jakarta.transaction.Transactional;
 
@Service
public class CompraService implements InterfazCompraService {
 
    @Autowired
    private ProductRepository productoRepository; // No inicializar con null
    @Autowired
    private CompraRepository compraRepository; // No inicializar con null
    @Autowired
    private UsuarioRepository usuarioRepository; // No inicializar con null
 
    @Transactional
    public void procesarCompra(CompraRequest request) {
        // Lista de ítems de compra (CompraItem) 
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
 
        Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(UsuarioNoEncontradoException::new);
       
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setUsuario(usuario);

        // Guardar primero la compra sin los ítems, para que tenga ID
        compra = compraRepository.save(compra);

        // Lista para guardar ítems con la compra asignada
        List<CompraItem> items = new ArrayList<>();
        double total = 0;

        for (CompraItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new ProductoNoEncontradoException());

            if (producto.getStock() < itemReq.getCantidad()) {
                throw new StockInsuficienteException();
            }

            producto.setStock(producto.getStock() - itemReq.getCantidad());
            productoRepository.save(producto);

            CompraItem item = new CompraItem();
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
            item.setCompra(compra); // acá ya tiene un ID válido
            total += producto.getPrecio() * itemReq.getCantidad(); // 
            items.add(item);
        }

        // Asignar los ítems y guardar la compra actualizada
        compra.setItems(items);
        compra.setTotal(total);
        compraRepository.save(compra);

    }
}