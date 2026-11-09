package cl.duoc.eco360.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Material reciclado disponible en bodega. El campo "producto" es el mismo
// nombre que usan los pedidos, y por eso ms-pedidos reserva stock con ese texto.
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String producto;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private String unidad;

    protected Producto() {
        // Requerido por JPA.
    }

    public Producto(String producto, int stock, String unidad) {
        this.producto = producto;
        this.stock = stock;
        this.unidad = unidad;
    }

    public Long getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}
