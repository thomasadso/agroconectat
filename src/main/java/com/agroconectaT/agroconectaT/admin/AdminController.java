package com.agroconectaT.agroconectaT.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioRepository;
import com.agroconectaT.agroconectaT.pedido.PedidoRepository;
import com.agroconectaT.agroconectaT.producto.ProductoRepository;
import com.agroconectaT.agroconectaT.pedido.Pedido;
import com.agroconectaT.agroconectaT.pedido.FacturaPDFService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    // Se eliminó pagoRepository porque no estaba siendo usado
    @Autowired private FacturaPDFService facturaPDFService;

    // --- DASHBOARD PRINCIPAL (Lo que ya tenías) ---
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsuarios = usuarioRepository.count();
        long totalProductos = productoRepository.count();
        List<Pedido> pedidos = pedidoRepository.findAll();
        double totalVentas = pedidos.stream().mapToDouble(Pedido::getTotal).sum();
        
        List<Pedido> pedidosRecientes = pedidos.stream()
                .sorted(Comparator.comparing(Pedido::getId).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("pedidos", pedidosRecientes);

        return "admin/dashboard"; 
    }

    // --- NUEVO: DETALLE PROFUNDO DE USUARIO ---
    @GetMapping("/usuario/{id}")
    public String verDetalleUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        model.addAttribute("usuario", usuario);

        if (usuario.getRol().equals("CAMPESINO")) {
            // Si es Campesino, cargamos sus productos para moderar
            model.addAttribute("productosCampesino", productoRepository.findByUsuario_Id(id));
        } else if (usuario.getRol().equals("COMPRADOR")) {
            // Si es Comprador, cargamos sus pedidos con historial completo
            List<Pedido> historial = pedidoRepository.findByUsuario_Id(id);
            model.addAttribute("historialCompras", historial);
        }
        
        return "admin/detalle_usuario"; // Vamos a crear esta vista nueva
    }

    // --- ACCIONES DE ADMINISTRACIÓN ---
    
    @GetMapping("/eliminar/usuario/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/eliminar/producto/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        // Al eliminar producto, redirigimos al dashboard o al detalle si veníamos de ahí
        // Por simplicidad, volvemos al dashboard
        productoRepository.deleteById(id);
        return "redirect:/admin/dashboard?msg=ProductoEliminado";
    }

    // Método para descargar factura (igual que antes)
    @GetMapping("/factura/{id}")
    public void generarFactura(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=AgroFactura_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        Pedido pedido = pedidoRepository.findById(id).get();
        facturaPDFService.exportar(response, pedido);
    }
}