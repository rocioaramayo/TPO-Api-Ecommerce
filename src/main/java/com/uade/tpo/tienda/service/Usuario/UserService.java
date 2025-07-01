package com.uade.tpo.tienda.service.Usuario;

import java.util.List;

import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Usuario;

public interface UserService {
    List<UsuarioResponse> listAllUsers();
    UsuarioResponse getMyProfile(String email);
    String habilitarUsuario(Long id);
    String deshabilitarUsuario(Long id);
    UsuarioResponse updateMyProfile(Usuario usuario, UsuarioUpdateRequest req);
    Usuario getUsuarioByEmail(String email);
}
