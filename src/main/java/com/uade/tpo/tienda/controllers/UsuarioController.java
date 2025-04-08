package com.uade.tpo.tienda.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.usuario.UsuarioService;

import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping
  public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
      Usuario nuevUsuario=usuarioService.crearUsuario(usuario);
      
      return ResponseEntity.ok(nuevUsuario);
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id){
    Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
    return usuarioOpt.map(ResponseEntity::ok)
    .orElse(ResponseEntity.notFound().build());
    
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
  
  



