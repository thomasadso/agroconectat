package com.agroconectaT.agroconectaT.pedido;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import com.agroconectaT.agroconectaT.producto.Producto;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
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

    @NotBlank
    @Size(max = 50)
    private String estado;

    @NotNull
    private LocalDateTime fecha;
}
