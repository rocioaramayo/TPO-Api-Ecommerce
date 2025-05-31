package com.uade.tpo.tienda.service.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.tienda.dto.AddressHistoryData;
import com.uade.tpo.tienda.dto.AddressHistoryResponse;
import com.uade.tpo.tienda.dto.PaymentHistoryData;
import com.uade.tpo.tienda.dto.PaymentHistoryResponse;
import com.uade.tpo.tienda.dto.ShippingHistoryData;
import com.uade.tpo.tienda.dto.ShippingHistoryResponse;
import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.enums.MetodoDePago;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import com.uade.tpo.tienda.service.compra.InterfazCompraService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioResponse> listAllUsers() {
        return usuarioRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse getUserById(Long id) {
        Usuario user = usuarioRepository.findById(id)
            .orElseThrow(UsuarioNoEncontradoException::new);
        return mapToResponse(user);
    }

    @Override
    public UsuarioResponse getMyProfile(String email) {
        Usuario me = usuarioRepository.findByEmail(email)
            .orElseThrow(UsuarioNoEncontradoException::new);
        return mapToResponse(me);
    }

    @Override
    @Transactional
    public String habilitarUsuario(Long id) {
        Usuario user = usuarioRepository.findById(id)
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con ID " + id + " no encontrado"));
        
        if (user.isActivo()) {
            return "El usuario " + user.getLoginName() + " ya se encuentra activo.";
        }
        
        user.setActivo(true);
        usuarioRepository.save(user);
        return "Usuario " + user.getLoginName()+ " habilitado exitosamente.";
    }

    @Override
    @Transactional
    public String deshabilitarUsuario(Long id) {
        Usuario user = usuarioRepository.findById(id)
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con ID " + id + " no encontrado"));
        
        if (!user.isActivo()) {
            return "El usuario " + user.getLoginName() + " ya se encuentra deshabilitado.";
        }
        
        user.setActivo(false);
        usuarioRepository.save(user);
        return "Usuario " + user.getLoginName() + " deshabilitado exitosamente.";
    }

    @Override
@Transactional
public UsuarioResponse updateMyProfile(Usuario usuario, UsuarioUpdateRequest req) {
    // Actualiza solo los campos permitidos
    usuario.setFirstName(req.getFirstName());
    usuario.setLastName(req.getLastName());
    usuarioRepository.save(usuario);
    return mapToResponse(usuario);
}



    private UsuarioResponse mapToResponse(Usuario u) {
        return new UsuarioResponse(
            u.getId(),
            u.getLoginName(),
            u.getEmail(),
            u.getFirstName(),
            u.getLastName(),
            u.getRole(),
            u.isActivo(),
            u.getCreatedAT()
        );
    }

    @Autowired
private InterfazCompraService compraService; // Agregar esta dependencia

public List<PaymentHistoryResponse> getPaymentHistory(String email) {
    List<Compra> compras = compraService.obtenerComprasDeUsuario(email);
    
    Map<MetodoDePago, PaymentHistoryData> paymentMap = new HashMap<>();
    
    for (Compra compra : compras) {
        if (compra.getMetodoDePago() != null) {
            MetodoDePago metodo = compra.getMetodoDePago();
            
            if (paymentMap.containsKey(metodo)) {
                PaymentHistoryData existing = paymentMap.get(metodo);
                existing.incrementarVeces();
                existing.actualizarUltimoUso(compra.getFecha());
                existing.agregarCuotas(compra.getCuotas());
            } else {
                PaymentHistoryData newData = new PaymentHistoryData();
                newData.setMetodo(metodo);
                newData.setVecesUtilizado(1);
                newData.setUltimoUso(compra.getFecha());
                newData.setCuotasUsadas(compra.getCuotas());
                paymentMap.put(metodo, newData);
            }
        }
    }
    
    return paymentMap.values().stream()
        .map(data -> PaymentHistoryResponse.builder()
            .metodoDePago(data.getMetodo())
            .cuotasUsadas(data.getCuotasUsadas())
            .ultimoUso(data.getUltimoUso())
            .vecesUtilizado(data.getVecesUtilizado())
            .build())
        .sorted((a, b) -> b.getUltimoUso().compareTo(a.getUltimoUso())) // Más recientes primero
        .collect(Collectors.toList());
}

public List<AddressHistoryResponse> getAddressHistory(String email) {
    List<Compra> compras = compraService.obtenerComprasDeUsuario(email);
    
    Map<String, AddressHistoryData> addressMap = new HashMap<>();
    
    for (Compra compra : compras) {
        if (compra.getDireccionEntrega() != null && !compra.getDireccionEntrega().isBlank()) {
            String key = compra.getDireccionEntrega() + "|" + 
                        (compra.getLocalidadEntrega() != null ? compra.getLocalidadEntrega() : "") + "|" +
                        (compra.getProvinciaEntrega() != null ? compra.getProvinciaEntrega() : "") + "|" +
                        (compra.getCodigoPostalEntrega() != null ? compra.getCodigoPostalEntrega() : "");
            
            if (addressMap.containsKey(key)) {
                AddressHistoryData existing = addressMap.get(key);
                existing.incrementarVeces();
                existing.actualizarUltimoUso(compra.getFecha());
            } else {
                AddressHistoryData newData = new AddressHistoryData();
                newData.setDireccion(compra.getDireccionEntrega());
                newData.setLocalidad(compra.getLocalidadEntrega());
                newData.setProvincia(compra.getProvinciaEntrega());
                newData.setCodigoPostal(compra.getCodigoPostalEntrega());
                newData.setTelefono(compra.getTelefonoContacto());
                newData.setVecesUtilizada(1);
                newData.setUltimoUso(compra.getFecha());
                addressMap.put(key, newData);
            }
        }
    }
    
    return addressMap.values().stream()
        .map(data -> AddressHistoryResponse.builder()
            .direccion(data.getDireccion())
            .localidad(data.getLocalidad())
            .provincia(data.getProvincia())
            .codigoPostal(data.getCodigoPostal())
            .telefono(data.getTelefono())
            .ultimoUso(data.getUltimoUso())
            .vecesUtilizada(data.getVecesUtilizada())
            .build())
        .sorted((a, b) -> b.getUltimoUso().compareTo(a.getUltimoUso())) // Más recientes primero
        .collect(Collectors.toList());
}

public List<ShippingHistoryResponse> getShippingHistory(String email) {
    List<Compra> compras = compraService.obtenerComprasDeUsuario(email);
    
    Map<String, ShippingHistoryData> shippingMap = new HashMap<>();
    
    for (Compra compra : compras) {
        if (compra.getMetodoEntrega() != null) {
            String key = compra.getMetodoEntrega().getNombre() + "|" + 
                        (compra.getPuntoRetiro() != null ? compra.getPuntoRetiro().getNombre() : "");
            
            if (shippingMap.containsKey(key)) {
                ShippingHistoryData existing = shippingMap.get(key);
                existing.incrementarVeces();
                existing.actualizarUltimoUso(compra.getFecha());
            } else {
                ShippingHistoryData newData = new ShippingHistoryData();
                newData.setMetodoEntrega(compra.getMetodoEntrega().getNombre());
                newData.setPuntoRetiro(compra.getPuntoRetiro() != null ? compra.getPuntoRetiro().getNombre() : null);
                newData.setVecesUtilizado(1);
                newData.setUltimoUso(compra.getFecha());
                shippingMap.put(key, newData);
            }
        }
    }
    
    return shippingMap.values().stream()
        .map(data -> ShippingHistoryResponse.builder()
            .metodoEntrega(data.getMetodoEntrega())
            .puntoRetiro(data.getPuntoRetiro())
            .ultimoUso(data.getUltimoUso())
            .vecesUtilizado(data.getVecesUtilizado())
            .build())
        .sorted((a, b) -> b.getUltimoUso().compareTo(a.getUltimoUso())) // Más recientes primero
        .collect(Collectors.toList());
}
}
