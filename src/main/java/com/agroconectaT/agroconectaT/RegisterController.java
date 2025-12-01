package com.agroconectaT.agroconectaT;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/register")
public class RegisterController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping
    public String processRegister(@RequestParam String username, @RequestParam String password, @RequestParam String rol, Model model) {
        if (!username.matches("^[A-Za-z0-9._%+-]+@agroconecta\\.com\\.co$")) {
            model.addAttribute("error", "El correo debe ser @agroconecta.com.co");
            return "register";
        }
        // Normalizar rol para evitar prefijos erróneos
        String rolNormalizado = rol.toUpperCase();
        if (rolNormalizado.startsWith("ROLE_")) {
            rolNormalizado = rolNormalizado.replace("ROLE_", "");
        }
        if (!rolNormalizado.equals("CAMPESINO") && !rolNormalizado.equals("COMPRADOR") && !rolNormalizado.equals("ADMIN")) {
            model.addAttribute("error", "El rol seleccionado no es válido");
            return "register";
        }
        if (usuarioService.buscarPorCorreo(username).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado");
            return "register";
        }
        Usuario usuario = Usuario.builder()
                .nombre(username.split("@")[0])
                .correo(username)
                .contrasena(new BCryptPasswordEncoder().encode(password))
                .rol(rolNormalizado)
                .build();
        usuarioService.guardar(usuario);
        return "redirect:/auth/login";
    }
}
