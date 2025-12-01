package com.agroconectaT.agroconectaT.mensaje;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    List<Mensaje> findByUsuario_Id(Integer usuarioId);
}
