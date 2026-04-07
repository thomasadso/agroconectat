package com.agroconectaT.agroconectaT.pedido;

import com.agroconectaT.agroconectaT.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    // Para las ventas del campesino (ordenadas por fecha)
    List<Pedido> findByProductoUsuarioOrderByFechaDesc(Usuario usuario);

    // EL BOTÓN QUE FALTABA: Para que el comprador vea sus compras pasadas
    List<Pedido> findByUsuario_Id(Integer usuarioId);
}