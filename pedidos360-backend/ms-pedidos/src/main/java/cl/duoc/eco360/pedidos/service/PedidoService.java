package cl.duoc.eco360.pedidos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.eco360.pedidos.client.InventarioClient;
import cl.duoc.eco360.pedidos.dto.PedidoRequest;
import cl.duoc.eco360.pedidos.exception.ApiException;
import cl.duoc.eco360.pedidos.model.EstadoPedido;
import cl.duoc.eco360.pedidos.model.Pedido;
import cl.duoc.eco360.pedidos.messaging.PedidosProducer;
import cl.duoc.eco360.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final InventarioClient inventario;
    private final PedidosProducer pedidosProducer;

    public PedidoService(PedidoRepository repository, InventarioClient inventario,
        PedidosProducer pedidosProducer) {
        this.pedidosProducer = pedidosProducer;
        this.repository = repository;
        this.inventario = inventario;
    }

    public List<Pedido> listar(EstadoPedido estado) {
        if (estado == null) {
            return repository.findAllByOrderByIdAsc();
        }
        return repository.findByEstadoOrderByIdAsc(estado);
    }

    public Pedido obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El pedido " + id + " no existe"));
    }

    // Reserva el stock en ms-inventario y luego guarda el pedido. Si guardar falla,
    // devuelve el stock reservado para no dejarlo bloqueado.
    public Pedido crear(PedidoRequest request) {
        String producto = request.producto().trim();
        int cantidad = request.cantidad();

        // 1. Reservar stock
        inventario.reservar(producto, cantidad);

        Pedido pedido;

        try {
        // 2. Guardar pedido en base de datos
            pedido = repository.save(
                new Pedido(producto, cantidad, EstadoPedido.EN_CLASIFICACION)
            );

        } catch (RuntimeException e) {
        // Si falla el guardado, devolver stock
            inventario.liberar(producto, cantidad);
            throw e;
        }

         // 3. Publicar evento en RabbitMQ
        try {
        pedidosProducer.publicarPedidoCreado(
                pedido.getId(),
                pedido.getProducto(),
                pedido.getCantidad()
            );
        } catch (RuntimeException e) {
        // El pedido ya fue guardado: no revertir stock ni simular que no existe
            System.err.println(
                "[RabbitMQ] Error al publicar evento del pedido "
                + pedido.getId() + ": " + e.getMessage()
            );
        }

        return pedido;
    }

    // Aplica la regla de transiciones. Al cancelar, el stock vuelve a inventario.
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtener(id);
        if (!pedido.getEstado().puedePasarA(nuevoEstado)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No se puede pasar de " + pedido.getEstado() + " a " + nuevoEstado);
        }
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            inventario.liberar(pedido.getProducto(), pedido.getCantidad());
        }
        pedido.setEstado(nuevoEstado);
        return repository.save(pedido);
    }

    // Solo se eliminan pedidos terminados; uno activo tiene stock reservado.
    @Transactional
    public void eliminar(Long id) {
        Pedido pedido = obtener(id);
        if (pedido.getEstado() != EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.DESPACHADO) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Solo se pueden eliminar pedidos CANCELADO o DESPACHADO; cancela el pedido primero");
        }
        repository.delete(pedido);
    }
}
