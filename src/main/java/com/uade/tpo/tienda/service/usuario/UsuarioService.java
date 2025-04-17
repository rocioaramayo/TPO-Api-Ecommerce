package com.uade.tpo.tienda.service.usuario;

import java.util.List;

import com.uade.tpo.tienda.entity.Usuario;

public interface UsuarioService {
  Usuario crearUsuario(Usuario usuario);
  Usuario obtenerUsuarioPorId(Long id) ;
  List<Usuario> listarUsuarios();
  Usuario actualizarUsuario(Long id, Usuario usuario);
  void eliminarUsuario(Long id);
}
