package cl.duoc.eco360.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.eco360.inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByIdAsc();

    Optional<Producto> findByProductoIgnoreCase(String producto);

    boolean existsByProductoIgnoreCase(String producto);

    // Descuento atomico: solo actualiza si alcanza el stock. Devuelve las filas
    // modificadas (0 = stock insuficiente), asi dos reservas simultaneas no
    // pueden dejar el stock en negativo.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Producto p set p.stock = p.stock - :cantidad "
            + "where lower(p.producto) = lower(:producto) and p.stock >= :cantidad")
    int descontar(@Param("producto") String producto, @Param("cantidad") int cantidad);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Producto p set p.stock = p.stock + :cantidad "
            + "where lower(p.producto) = lower(:producto)")
    int reponer(@Param("producto") String producto, @Param("cantidad") int cantidad);
}
