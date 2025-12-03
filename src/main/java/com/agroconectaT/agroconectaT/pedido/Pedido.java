package com.agroconectaT.agroconectaT.pedido;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; 
import lombok.*;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.producto.Producto;
import java.time.LocalDateTime; // Necesario para la fecha

@Entity
@Table(name = "pedido")
@Data // Esto crea automáticamente getTotal(), getFecha(), getId(), etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @NotNull
    @Min(1)
    private Integer cantidad;

    // --- ESTOS SON LOS CAMPOS QUE FALTABAN ---
    @NotNull
    private Double total;  // ¡Esto arregla el error getTotal()!

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha; // ¡Esto arregla el error getFecha()!
    // -----------------------------------------

    @NotBlank
    private String estado;

    // Asignar fecha automática antes de guardar
    @PrePersist
    public void asignarFecha() {
        this.fecha = LocalDateTime.now();
    }
}