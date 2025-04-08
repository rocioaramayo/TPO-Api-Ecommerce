package com.uade.tpo.tienda.service.usuario;

import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.enums.Role;
import com.uade.tpo.tienda.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;



    @Override
    public Usuario crearUsuario(Usuario usuario) {
        // Validar que el nombre de usuario no esté en uso
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        // Validar que el email no esté en uso
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        
        // Validar que la contraseña cumpla ciertos criterios (por ejemplo, mínimo 8 caracteres)
        if (usuario.getPassword() == null || usuario.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        
        // Encriptar la contraseña usando PasswordEncoder (por ejemplo, BCrypt)
        usuario.setPassword(usuario.getPassword());
        
        // Si no se especifica un rol, se puede asignar uno por defecto (ejemplo: COMPRADOR)
        if (usuario.getRole() == null) {
            usuario.setRole(Role.COMPRADOR);
        }
        
        // Guardar el usuario en la base de datos y devolver el usuario creado
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
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
            
            // Actualiza la contraseña solo si se envía una nueva y no vacía
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuario.getPassword());
            }
            
            return usuarioRepository.save(usuarioExistente);
        }
        return null;
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }


}
