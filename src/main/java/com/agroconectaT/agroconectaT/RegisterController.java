package com.agroconectaT.agroconectaT;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/auth/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/auth/register")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // 1. Verificamos que el correo no exista ya
        if (usuarioService.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "Ese correo ya está registrado en AgroConecta.");
            return "register";
        }

        // 2. ENCRIPTAMOS LA CONTRASEÑA ANTES DE GUARDARLA
        String claveEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(claveEncriptada);

        // 3. Verificamos que no se intenten colar como ADMIN
        if (usuario.getRol() == null || usuario.getRol().equals("ADMIN")) {
            usuario.setRol("COMPRADOR"); // Por defecto si hacen trampa
        }

        // 4. Guardamos en base de datos
        usuarioService.guardar(usuario);

        // 5. Redirigimos al login con mensaje de éxito
        return "redirect:/auth/login?registrado=true";
    }
}