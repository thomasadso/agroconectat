package com.agroconectaT.agroconectaT.pago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    @Autowired
    private PagoService pagoService;

    @GetMapping
    public String listarPagos(Model model) {
        List<Pago> pagos = pagoService.listarTodos();
        model.addAttribute("pagos", pagos);
        return "pagos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pago", new Pago());
        return "pagos/formulario";
    }

    @PostMapping
    public String guardarPago(@ModelAttribute Pago pago) {
        pagoService.guardar(pago);
        return "redirect:/pagos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Pago pago = pagoService.buscarPorId(id).orElseThrow();
        model.addAttribute("pago", pago);
        return "pagos/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPago(@PathVariable Integer id, @ModelAttribute Pago pago) {
        pago.setId(id);
        pagoService.guardar(pago);
        return "redirect:/pagos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPago(@PathVariable Integer id) {
        pagoService.eliminar(id);
        return "redirect:/pagos";
    }
}
