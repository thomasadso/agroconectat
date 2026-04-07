package com.agroconectaT.agroconectaT.producto;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByUsuario(Usuario usuario);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}