package cl.duoc.eco360.inventario.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.eco360.inventario.dto.MovimientoRequest;
import cl.duoc.eco360.inventario.dto.ProductoRequest;
import cl.duoc.eco360.inventario.exception.ApiException;
import cl.duoc.eco360.inventario.model.Producto;
import cl.duoc.eco360.inventario.repository.ProductoRepository;

@Service
public class InventarioService {

    private final ProductoRepository repository;

    public InventarioService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAllByOrderByIdAsc();
    }

    public Producto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto " + id + " no existe"));
    }

    @Transactional
    public Producto crear(ProductoRequest request) {
        String nombre = request.producto().trim();
        if (repository.existsByProductoIgnoreCase(nombre)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un producto llamado '" + nombre + "'");
        }
        return repository.save(new Producto(nombre, request.stock(), request.unidad().trim()));
    }

    @Transactional
    public Producto actualizar(Long id, ProductoRequest request) {
        Producto existente = obtener(id);
        String nombre = request.producto().trim();
        repository.findByProductoIgnoreCase(nombre)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Ya existe un producto llamado '" + nombre + "'");
                });
        existente.setProducto(nombre);
        existente.setStock(request.stock());
        existente.setUnidad(request.unidad().trim());
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    // Descuenta stock para un pedido. 404 si el producto no existe, 409 si no alcanza.
    @Transactional
    public Producto reservar(MovimientoRequest movimiento) {
        Producto producto = buscarPorNombre(movimiento.producto());
        int filas = repository.descontar(producto.getProducto(), movimiento.cantidad());
        if (filas == 0) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Stock insuficiente de '" + producto.getProducto() + "': disponible "
                            + producto.getStock() + " " + producto.getUnidad()
                            + ", solicitado " + movimiento.cantidad());
        }
        return buscarPorNombre(movimiento.producto());
    }

    // Devuelve stock cuando un pedido se cancela.
    @Transactional
    public Producto liberar(MovimientoRequest movimiento) {
        Producto producto = buscarPorNombre(movimiento.producto());
        repository.reponer(producto.getProducto(), movimiento.cantidad());
        return buscarPorNombre(movimiento.producto());
    }

    private Producto buscarPorNombre(String nombre) {
        return repository.findByProductoIgnoreCase(nombre.trim())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "El producto '" + nombre + "' no existe en el inventario"));
    }
}
