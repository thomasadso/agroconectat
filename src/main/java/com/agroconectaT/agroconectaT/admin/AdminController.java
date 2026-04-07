package com.agroconectaT.agroconectaT.admin;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;
import com.agroconectaT.agroconectaT.producto.ProductoRepository;
import com.agroconectaT.agroconectaT.pedido.Pedido;
import com.agroconectaT.agroconectaT.pedido.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Pedido> todosLosPedidos = pedidoRepository.findAll();
        List<Usuario> todosLosUsuarios = usuarioService.listarTodos(); // Asegúrate de tener este método en tu Service

        // Cálculos para el Admin
        double ventasTotales = todosLosPedidos.stream().mapToDouble(Pedido::getTotal).sum();
        long totalCampesinos = todosLosUsuarios.stream().filter(u -> u.getRol().equals("CAMPESINO")).count();
        long totalCompradores = todosLosUsuarios.stream().filter(u -> u.getRol().equals("COMPRADOR")).count();

        model.addAttribute("ventasTotales", ventasTotales);
        model.addAttribute("numPedidos", todosLosPedidos.size());
        model.addAttribute("numProductos", productoRepository.count());
        model.addAttribute("numUsuarios", todosLosUsuarios.size());
        model.addAttribute("numCampesinos", totalCampesinos);
        model.addAttribute("numCompradores", totalCompradores);
        model.addAttribute("ultimosPedidos", todosLosPedidos);

        return "admin/dashboard";
    }
}