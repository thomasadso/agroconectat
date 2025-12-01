package com.agroconectaT.agroconectaT.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarTodos();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        return "pedidos/formulario";
    }

    @PostMapping
    public String guardarPedido(@ModelAttribute Pedido pedido) {
        pedidoService.guardar(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();
        model.addAttribute("pedido", pedido);
        return "pedidos/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPedido(@PathVariable Integer id, @ModelAttribute Pedido pedido) {
        pedido.setId(id);
        pedidoService.guardar(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Integer id) {
        pedidoService.eliminar(id);
        return "redirect:/pedidos";
    }
}
