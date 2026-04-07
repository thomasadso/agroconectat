package com.agroconectaT.agroconectaT.producto;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.usuario.UsuarioService;
import com.agroconectaT.agroconectaT.pedido.Pedido;
import com.agroconectaT.agroconectaT.pedido.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("")
    public String listarMisProductos(Model model, Authentication auth) {
        if (auth == null) return "redirect:/auth/login";
        String correo = auth.getName();
        Usuario campesino = usuarioService.buscarPorCorreo(correo).orElse(null);

        if (campesino != null) {
            List<Producto> misProductos = productoRepository.findByUsuario(campesino);
            List<Pedido> misVentas = pedidoRepository.findByProductoUsuarioOrderByFechaDesc(campesino);

            model.addAttribute("productos", misProductos);
            model.addAttribute("misVentas", misVentas);
            model.addAttribute("gananciasTotales", misVentas.stream().mapToDouble(Pedido::getTotal).sum());
            model.addAttribute("unidadesVendidas", misVentas.stream().mapToInt(Pedido::getCantidad).sum());
            model.addAttribute("totalProductos", misProductos.size());
            return "productos/lista";
        }
        return "redirect:/";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, 
                                  @RequestParam("file") MultipartFile multipartFile,
                                  Authentication auth) throws IOException {
        
        String correo = auth.getName();
        Usuario campesino = usuarioService.buscarPorCorreo(correo).orElse(null);
        producto.setUsuario(campesino);

        // Si no vienen coordenadas del mapa, ponemos unas por defecto (Bogotá)
        if(producto.getLatitud() == null) producto.setLatitud(4.6097);
        if(producto.getLongitud() == null) producto.setLongitud(-74.0817);

        if (!multipartFile.isEmpty()) {
            // Nombre único para la foto
            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                producto.setImagenUrl("/uploads/" + fileName);
            }
        } else if (producto.getId() == null) {
            producto.setImagenUrl("/img/campo-colombia.jpg"); 
        }
        
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        model.addAttribute("producto", producto);
        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }
}