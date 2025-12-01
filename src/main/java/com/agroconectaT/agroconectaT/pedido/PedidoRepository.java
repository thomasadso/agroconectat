package com.agroconectaT.agroconectaT.pedido;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuario_Id(Integer usuarioId);
}
