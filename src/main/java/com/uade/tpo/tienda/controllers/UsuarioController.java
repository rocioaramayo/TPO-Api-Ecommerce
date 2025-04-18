package com.uade.tpo.tienda.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.service.usuario.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("usuarios")
public class UsuarioController {
  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/{id}")
public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
    try {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    } catch (UsuarioNoEncontradoException e) {
        return ResponseEntity.notFound().build();
    }
}

  @GetMapping
  public ResponseEntity<List<Usuario>>listarUsuarios() {
    List<Usuario> usuarios= usuarioService.listarUsuarios();
    return ResponseEntity.ok(usuarios);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
    Usuario usuarioActualizado= usuarioService.actualizarUsuario(id, usuario);
    if (usuarioActualizado == null) {
      return ResponseEntity.notFound().build();// hacer nuestras execepciones
  }
     return ResponseEntity.ok(usuarioActualizado);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Usuario> eliminarUsuario(@PathVariable Long id)
  {
    usuarioService.eliminarUsuario(id);
    return ResponseEntity.noContent().build();

  }


}
  
  



