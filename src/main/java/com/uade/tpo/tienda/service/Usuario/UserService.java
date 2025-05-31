package com.uade.tpo.tienda.service.Usuario;

import java.util.List;

import com.uade.tpo.tienda.dto.AddressHistoryResponse;
import com.uade.tpo.tienda.dto.PaymentHistoryResponse;
import com.uade.tpo.tienda.dto.ShippingHistoryResponse;
import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Usuario;

public interface UserService {
    List<UsuarioResponse> listAllUsers();
    UsuarioResponse getUserById(Long id);
    UsuarioResponse getMyProfile(String email);
    String habilitarUsuario(Long id);
    String deshabilitarUsuario(Long id);
    UsuarioResponse updateMyProfile(Usuario usuario, UsuarioUpdateRequest req);
    List<PaymentHistoryResponse> getPaymentHistory(String email);
    List<AddressHistoryResponse> getAddressHistory(String email);
    List<ShippingHistoryResponse> getShippingHistory(String email);
}
