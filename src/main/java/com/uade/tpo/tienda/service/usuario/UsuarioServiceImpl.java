package com.uade.tpo.tienda.service.usuario;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService{

  @Autowired
  private UsuarioRepository usuarioRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(UsuarioNoEncontradoException::new);
    }
    

  @Override
  public List<Usuario> listarUsuarios() {
    return usuarioRepository.findAll();
  }

  @Override
  public Usuario actualizarUsuario(Long id, Usuario usuario) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
    if (usuarioOpt.isPresent()) {
        Usuario usuarioExistente = usuarioOpt.get();
        // Actualiza los campos necesarios
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setFirstName(usuario.getFirstName());
        usuarioExistente.setLastName(usuario.getLastName());
        usuarioExistente.setRole(usuario.getRole());
         // Actualiza la contraseña solo si se envía una nueva y no es vacía
         if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
          usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
      }
        return usuarioRepository.save(usuarioExistente);
    }
    return null; // O lanzar una excepción personalizada
  }

  @Override
  public void desactivarUsuario(Long id) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
    if (!usuarioOpt.isPresent()) {
      throw new UsuarioNoEncontradoException();
    }
    Usuario existing = usuarioOpt.get();
    existing.setActivo(false);
    usuarioRepository.save(existing);
    
  }
  
}
