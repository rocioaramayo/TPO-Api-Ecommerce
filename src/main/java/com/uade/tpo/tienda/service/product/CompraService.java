package com.uade.tpo.tienda.service.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.dto.CompraItemRequest;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final ProductRepository productoRepository = null;
    private final CompraRepository compraRepository = null;

    @Transactional
    public void procesarCompra(CompraRequest request) {
        List<Compra> items = new ArrayList<>();

        for (CompraItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < itemReq.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - itemReq.getCantidad());
            productoRepository.save(producto);

            Compra item = new Compra();
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
            items.add(item);
        }

        Compra compra = new Compra();
        compra.setUsuarioId(request.getUsuarioId());
        compra.setItems(items);
        compra.setFecha(LocalDateTime.now());

        compraRepository.save(compra);
    }
}

