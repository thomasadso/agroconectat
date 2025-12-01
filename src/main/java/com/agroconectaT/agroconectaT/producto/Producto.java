package com.agroconectaT.agroconectaT.producto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.agroconectaT.agroconectaT.usuario.Usuario;

@Entity @Table(name = "producto")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @NotBlank private String nombre;
    @Size(max = 2000) private String descripcion;
    @NotNull @DecimalMin("0.0") private Double precio;
    private String imagenUrl;
    @NotBlank private String categoria;
    @NotBlank private String ubicacion; // ¡CRUCIAL PARA BÚSQUEDA!

    @ManyToOne @JoinColumn(name = "usuario_id") private Usuario usuario;
    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL) private Inventario inventario;
}