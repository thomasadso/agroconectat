package com.agroconectaT.agroconectaT.mensaje;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {
    @Autowired
    private MensajeRepository mensajeRepository;

    public List<Mensaje> listarTodos() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> buscarPorId(Integer id) {
        return mensajeRepository.findById(id);
    }

    public List<Mensaje> buscarPorUsuario(Integer usuarioId) {
        return mensajeRepository.findByUsuario_Id(usuarioId);
    }

    public Mensaje guardar(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public void eliminar(Integer id) {
        mensajeRepository.deleteById(id);
    }
}
