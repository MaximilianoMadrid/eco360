package cl.duoc.eco360.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// La base de pruebas va en memoria para no crear archivos en ./data al compilar.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class MsPedidosApplicationTests {

    @Test
    void contextLoads() {
    }
}
