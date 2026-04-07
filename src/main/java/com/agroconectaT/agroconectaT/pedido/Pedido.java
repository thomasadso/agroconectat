package com.agroconectaT.agroconectaT.pedido;

import com.agroconectaT.agroconectaT.producto.Producto;
import com.agroconectaT.agroconectaT.usuario.Usuario;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cantidad;
    private String estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private Double total;

    // CONEXIÓN CON PRODUCTO (LA LLAVE FORÁNEA)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false) 
    private Producto producto;

    // CONEXIÓN CON USUARIO (EL COMPRADOR)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // --- CONSTRUCTOR VACÍO (Obligatorio para Hibernate) ---
    public Pedido() {
    }

    // --- GETTERS Y SETTERS (Fundamentales para que Java lea los datos) ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}