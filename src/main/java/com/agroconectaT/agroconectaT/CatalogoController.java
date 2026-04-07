package com.agroconectaT.agroconectaT;

import com.agroconectaT.agroconectaT.producto.Producto;
import com.agroconectaT.agroconectaT.producto.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String verCatalogo(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "catalogo";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") Integer id, Model model) {
        Producto p = productoRepository.findById(id).orElse(null);
        if (p == null) return "redirect:/catalogo";
        
        model.addAttribute("producto", p);
        return "catalogo_detalle";
    }
}