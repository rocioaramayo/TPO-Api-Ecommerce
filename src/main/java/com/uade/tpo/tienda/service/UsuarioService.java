package com.uade.tpo.tienda.service;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.tienda.entity.Usuario;

public interface UsuarioService {
  Usuario crearUsuario(Usuario usuario);
  Optional<Usuario> obtenerUsuarioPorId(Long id);
  List<Usuario> listarUsuarios();
  Usuario actualizarUsuario(Long id, Usuario usuario);
  void eliminarUsuario(Long id);
}
