package cl.duoc.eco360.inventario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.eco360.inventario.model.Producto;
import cl.duoc.eco360.inventario.repository.ProductoRepository;

// Carga materiales de ejemplo la primera vez (base vacia). Los nombres coinciden
// con los de los pedidos de ejemplo de ms-pedidos.
@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataLoader(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new Producto("Botellas PET", 500, "kg"));
        repository.save(new Producto("Carton prensado", 300, "kg"));
        repository.save(new Producto("Vidrio molido", 200, "kg"));
        repository.save(new Producto("Aluminio", 150, "kg"));
    }
}
