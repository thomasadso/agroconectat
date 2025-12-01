package com.agroconectaT.agroconectaT.mensaje;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/mensajes")
public class MensajeController {
    @Autowired
    private MensajeService mensajeService;

    @GetMapping
    public String listarMensajes(Model model) {
        List<Mensaje> mensajes = mensajeService.listarTodos();
        model.addAttribute("mensajes", mensajes);
        return "mensajes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("mensaje", new Mensaje());
        return "mensajes/formulario";
    }

    @PostMapping
    public String guardarMensaje(@ModelAttribute Mensaje mensaje) {
        mensajeService.guardar(mensaje);
        return "redirect:/mensajes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Mensaje mensaje = mensajeService.buscarPorId(id).orElseThrow();
        model.addAttribute("mensaje", mensaje);
        return "mensajes/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarMensaje(@PathVariable Integer id, @ModelAttribute Mensaje mensaje) {
        mensaje.setId(id);
        mensajeService.guardar(mensaje);
        return "redirect:/mensajes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMensaje(@PathVariable Integer id) {
        mensajeService.eliminar(id);
        return "redirect:/mensajes";
    }
}
