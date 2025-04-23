package com.uade.tpo.tienda.service.Usuario;

import java.util.List;
import com.uade.tpo.tienda.dto.UsuarioResponse;

public interface UserService {
    List<UsuarioResponse> listAllUsers();
    UsuarioResponse getUserById(Long id);
    UsuarioResponse getMyProfile(String email);
    void habilitarUsuario(Long id);
    void deshabilitarUsuario(Long id);
}
