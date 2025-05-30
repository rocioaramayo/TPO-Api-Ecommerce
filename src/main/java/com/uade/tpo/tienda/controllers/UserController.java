package com.uade.tpo.tienda.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import com.uade.tpo.tienda.dto.AddressHistoryResponse;
import com.uade.tpo.tienda.dto.PaymentHistoryResponse;
import com.uade.tpo.tienda.dto.ShippingHistoryResponse;
import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.Usuario.UserService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService ;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listAll() {
        return ResponseEntity.ok(userService.listAllUsers());
    }


    @GetMapping("/{id}")

    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(userService.getMyProfile(usuario.getEmail()));
    }



    @PutMapping("/{id}/habilitar")
    public ResponseEntity<Map<String, String>> habilitar(@PathVariable Long id) {
        String mensaje = userService.habilitarUsuario(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }

@PutMapping("/me")
public ResponseEntity<UsuarioResponse> updateProfile(
    @AuthenticationPrincipal Usuario usuario,
    @RequestBody UsuarioUpdateRequest req
) {
    UsuarioResponse updated = userService.updateMyProfile(usuario, req);
    return ResponseEntity.ok(updated);
}


    @PutMapping("/{id}/deshabilitar")
    public ResponseEntity<Map<String, String>> deshabilitar(@PathVariable Long id) {
        String mensaje = userService.deshabilitarUsuario(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/payment-history")
    public ResponseEntity<List<PaymentHistoryResponse>> getMyPaymentHistory(
            @AuthenticationPrincipal Usuario usuario) {
        List<PaymentHistoryResponse> history = userService.getPaymentHistory(usuario.getEmail());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/me/addresses-history") 
    public ResponseEntity<List<AddressHistoryResponse>> getMyAddressHistory(
            @AuthenticationPrincipal Usuario usuario) {
        List<AddressHistoryResponse> addresses = userService.getAddressHistory(usuario.getEmail());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/me/shipping-history")
    public ResponseEntity<List<ShippingHistoryResponse>> getMyShippingHistory(
            @AuthenticationPrincipal Usuario usuario) {
        List<ShippingHistoryResponse> shipping = userService.getShippingHistory(usuario.getEmail());
        return ResponseEntity.ok(shipping);
    }

}
