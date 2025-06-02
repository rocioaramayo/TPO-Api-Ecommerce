package com.uade.tpo.tienda.service.direccion;

import com.uade.tpo.tienda.dto.DireccionRequest;
import com.uade.tpo.tienda.dto.DireccionResponse;
import com.uade.tpo.tienda.entity.Direccion;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.repository.DireccionRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<DireccionResponse> obtenerDireccionesDelUsuario(String email) {
        return direccionRepository.findAllByUsuarioEmail(email).stream()
                .map(this::mapear)
                .toList();
    }

    @Override
    public void guardarDireccion(String email, DireccionRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .numero(request.getNumero())
                .piso(request.getPiso())
                .departamento(request.getDepartamento())
                .localidad(request.getLocalidad())
                .provincia(request.getProvincia())
                .codigoPostal(request.getCodigoPostal())
                .telefonoContacto(request.getTelefonoContacto())
                .usuario(usuario)
                .build();

        direccionRepository.save(direccion);
    }

    private DireccionResponse mapear(Direccion dir) {
        return DireccionResponse.builder()
                .id(dir.getId())
                .calle(dir.getCalle())
                .numero(dir.getNumero())
                .piso(dir.getPiso())
                .departamento(dir.getDepartamento())
                .localidad(dir.getLocalidad())
                .provincia(dir.getProvincia())
                .codigoPostal(dir.getCodigoPostal())
                .telefonoContacto(dir.getTelefonoContacto())
                .build();
    }
}
