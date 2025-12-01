package com.agroconectaT.agroconectaT.producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;

@Controller @RequestMapping("/productos")
public class ProductoController {
    @Autowired private ProductoService productoService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public String listarProductos(Model model, Authentication auth) {
        // En futuro: Filtrar si es campesino para que vea solo los suyos
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping
    public String guardarProducto(@ModelAttribute Producto producto, Authentication auth) {
        var usuario = usuarioService.buscarPorCorreo(auth.getName()).orElseThrow();
        producto.setUsuario(usuario); // Asigna el dueño automáticamente
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id).orElseThrow());
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth) {
        var prod = productoService.buscarPorId(id).orElse(null);
        if (prod != null) {
            boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean isOwner = prod.getUsuario().getCorreo().equals(auth.getName());
            if (isAdmin || isOwner) productoService.eliminar(id);
        }
        return "redirect:/productos";
    }
}