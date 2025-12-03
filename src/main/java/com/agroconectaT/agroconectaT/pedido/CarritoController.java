package com.agroconectaT.agroconectaT.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.agroconectaT.agroconectaT.producto.Producto;
import com.agroconectaT.agroconectaT.producto.ProductoRepository;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PedidoRepository pedidoRepository;

    @GetMapping
    @SuppressWarnings("unchecked")
    public String verCarrito(HttpSession session, Model model) {
        List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();
        double totalCompra = carrito.stream().mapToDouble(Pedido::getTotal).sum();
        model.addAttribute("carrito", carrito);
        model.addAttribute("totalCompra", totalCompra);
        return "pedidos/carrito";
    }

    @GetMapping("/agregar/{idProducto}")
    @SuppressWarnings("unchecked")
    public String agregarAlCarrito(@PathVariable Integer idProducto, HttpSession session) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
            if (carrito == null) carrito = new ArrayList<>();
            Pedido item = new Pedido();
            item.setProducto(producto);
            item.setCantidad(1);
            item.setTotal(producto.getPrecio());
            carrito.add(item);
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito";
    }

    @GetMapping("/checkout")
    @SuppressWarnings("unchecked")
    public String checkout(HttpSession session, Model model) {
        List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) return "redirect:/carrito";
        double total = carrito.stream().mapToDouble(Pedido::getTotal).sum();
        model.addAttribute("total", total);
        return "pedidos/pasarela_pago";
    }

    // --- PROCESAR PAGO Y GENERAR DATOS PARA FACTURA EN PANTALLA ---
    @PostMapping("/procesar-pago")
    @SuppressWarnings("unchecked")
    public String procesarPago(@RequestParam("banco") String banco, Authentication auth, HttpSession session, Model model) {
        String correoUsuario = auth.getName();
        Usuario comprador = usuarioRepository.findByCorreo(correoUsuario).get();
        List<Pedido> carrito = (List<Pedido>) session.getAttribute("carrito");
        
        List<Pedido> pedidosGuardados = new ArrayList<>();
        double granTotal = 0;

        if (carrito != null) {
            for (Pedido itemTemp : carrito) {
                Pedido pedidoReal = new Pedido();
                pedidoReal.setUsuario(comprador);
                pedidoReal.setProducto(itemTemp.getProducto());
                pedidoReal.setCantidad(itemTemp.getCantidad());
                pedidoReal.setTotal(itemTemp.getTotal());
                pedidoReal.setEstado("PAGADO (" + banco + ")");
                
                pedidoRepository.save(pedidoReal);
                pedidosGuardados.add(pedidoReal);
                granTotal += pedidoReal.getTotal();
            }
        }
        
        // --- DATOS PARA QUE LA PANTALLA SE VEA PRO ---
        model.addAttribute("pedidos", pedidosGuardados); // Lista de items comprados
        model.addAttribute("total", granTotal);          // Total pagado
        model.addAttribute("comprador", comprador);      // Datos del cliente
        model.addAttribute("banco", banco);              // Banco seleccionado
        model.addAttribute("fecha", LocalDateTime.now());// Fecha y hora exacta
        
        // ID para generar el PDF (usamos el del último item como referencia)
        if (!pedidosGuardados.isEmpty()) {
            model.addAttribute("ultimoPedidoId", pedidosGuardados.get(pedidosGuardados.size() - 1).getId());
        }

        session.removeAttribute("carrito"); // Vaciamos el carrito
        return "pedidos/exito"; // Mostramos la factura digital
    }
}