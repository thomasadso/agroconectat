package com.agroconectaT.agroconectaT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.agroconectaT.agroconectaT.producto.ProductoService;

@Controller
public class CatalogoDetalleController {
    @Autowired
    private ProductoService productoService;

    @GetMapping("/catalogo/detalle/{id}")
    public String detalleProducto(@PathVariable Integer id, Model model) {
        var producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);
        return "catalogo_detalle";
    }
}