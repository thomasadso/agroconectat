package com.agroconectaT.agroconectaT.pago;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.agroconectaT.agroconectaT.pedido.Pedido;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull
    @DecimalMin("0.0")
    private Double monto;

    @NotBlank
    @Size(max = 50)
    private String metodo;

    @NotNull
    private java.time.LocalDateTime fecha;
}
