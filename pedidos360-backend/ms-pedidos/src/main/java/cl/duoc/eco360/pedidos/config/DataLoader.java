package cl.duoc.eco360.pedidos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.eco360.pedidos.model.EstadoPedido;
import cl.duoc.eco360.pedidos.model.Pedido;
import cl.duoc.eco360.pedidos.repository.PedidoRepository;

// Pedidos de ejemplo la primera vez (base vacia): los mismos tres que devolvia
// la version anterior en memoria. Se cargan directo en la base, sin pasar por
// inventario, por eso no descuentan stock.
@Component
public class DataLoader implements CommandLineRunner {

    private final PedidoRepository repository;

    public DataLoader(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new Pedido("Botellas PET", 40, EstadoPedido.EN_CLASIFICACION));
        repository.save(new Pedido("Carton prensado", 25, EstadoPedido.LISTO_PARA_DESPACHO));
        repository.save(new Pedido("Vidrio molido", 60, EstadoPedido.EN_PROCESO));
    }
}
