package com.agroconectaT.agroconectaT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.agroconectaT.agroconectaT.producto.ProductoService;
import com.agroconectaT.agroconectaT.producto.Producto;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
public class CatalogoController {
    @Autowired private ProductoService productoService;

    @GetMapping("/catalogo")
    public String mostrarCatalogo(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String orden, Model model) {
        
        var productos = productoService.listarTodos().stream();

        if (busqueda != null && !busqueda.isBlank()) 
            productos = productos.filter(p -> p.getNombre().toLowerCase().contains(busqueda.toLowerCase()));
        if (ubicacion != null && !ubicacion.isBlank()) 
            productos = productos.filter(p -> p.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()));
        if (categoria != null && !categoria.isBlank()) 
            productos = productos.filter(p -> p.getCategoria().equalsIgnoreCase(categoria));
        
        var lista = productos.collect(Collectors.toList());
        
        if ("precio_asc".equals(orden)) lista.sort(Comparator.comparing(Producto::getPrecio));
        if ("precio_desc".equals(orden)) lista.sort(Comparator.comparing(Producto::getPrecio).reversed());

        model.addAttribute("productos", lista);
        return "catalogo";
    }
}