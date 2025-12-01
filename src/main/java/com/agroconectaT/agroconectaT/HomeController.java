package com.agroconectaT.agroconectaT;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index", "/home"})
    public String home() {
        return "index";
    }

    @GetMapping("/simular-error")
    public String simularError() {
        throw new RuntimeException("Error de prueba para sustentación");
    }
}