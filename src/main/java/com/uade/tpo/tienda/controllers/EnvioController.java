package com.uade.tpo.tienda.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/envio")
public class EnvioController {

    @GetMapping
    public Map<String, Integer> calcularEnvio(@RequestParam String zona) {
        int costo;

        switch (zona.toLowerCase()) {
            case "local":
                costo = 500;
                break;
            case "nacional":
                costo = 1000;
                break;
            case "internacional":
                costo = 2500;
                break;
            default:
                costo = 0;
        }

        return Map.of("costo", costo);
    }
}

