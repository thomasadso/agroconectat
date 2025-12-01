package com.agroconectaT.agroconectaT.usuario;

import jakarta.persistence.*;
import lombok.*;
import com.agroconectaT.agroconectaT.producto.Producto;
import com.agroconectaT.agroconectaT.pedido.Pedido;
import java.util.List;

@Entity @Table(name = "usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(nullable = false) private String nombre;
    @Column(nullable = false, unique = true) private String correo;
    @Column(nullable = false) private String contrasena;
    @Column(nullable = false) private String rol; // ADMIN, CAMPESINO, COMPRADOR
    private String telefono;
    private String direccion;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) private List<Producto> productos;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) private List<Pedido> pedidos;
}