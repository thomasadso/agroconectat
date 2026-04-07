package com.agroconectaT.agroconectaT.pedido;

import com.agroconectaT.agroconectaT.producto.Producto;
import com.agroconectaT.agroconectaT.producto.ProductoRepository;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FacturaPDFService facturaPDFService;

    @PostMapping("/comprar")
    public String procesarCompra(@RequestParam("productoId") Integer productoId, 
                                 @RequestParam("cantidad") Integer cantidad, 
                                 Authentication auth) {
        try {
            if (auth == null) return "redirect:/auth/login";

            Producto producto = productoRepository.findById(productoId).orElse(null);
            Usuario comprador = usuarioService.buscarPorCorreo(auth.getName()).orElse(null);

            if (producto != null && comprador != null) {
                Pedido pedido = new Pedido();
                pedido.setProducto(producto);
                pedido.setUsuario(comprador);
                pedido.setCantidad(cantidad);
                pedido.setTotal(producto.getPrecio() * cantidad);
                pedido.setFecha(new Date());
                pedido.setEstado("PAGADO");
                
                Pedido guardado = pedidoRepository.save(pedido);
                return "redirect:/pedidos/exito/" + guardado.getId();
            }
        } catch (Exception e) {
            System.out.println("ERROR EN COMPRA: " + e.getMessage());
        }
        return "redirect:/catalogo";
    }

    @GetMapping("/exito/{id}")
    public String compraExitosa(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("pedidoId", id);
        return "pedidos/exito";
    }

    @GetMapping("/factura/{id}")
    public void descargarFactura(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Factura_AgroConecta.pdf");
            facturaPDFService.exportar(response, pedido);
        }
    }
}