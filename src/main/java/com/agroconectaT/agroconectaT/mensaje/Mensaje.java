package com.agroconectaT.agroconectaT.mensaje;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotBlank
    @Size(max = 50)
    private String tipo;

    @NotBlank
    @Size(max = 2000)
    private String contenido;

    @NotNull
    private LocalDateTime fecha;
}
