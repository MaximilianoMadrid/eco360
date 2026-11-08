package cl.duoc.eco360.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.eco360.pedidos.model.EstadoPedido;
import cl.duoc.eco360.pedidos.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByIdAsc();

    List<Pedido> findByEstadoOrderByIdAsc(EstadoPedido estado);
}
