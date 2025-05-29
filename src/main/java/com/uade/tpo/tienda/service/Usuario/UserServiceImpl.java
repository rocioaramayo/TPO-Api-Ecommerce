package com.uade.tpo.tienda.service.Usuario;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.UsuarioRepository;

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
}
