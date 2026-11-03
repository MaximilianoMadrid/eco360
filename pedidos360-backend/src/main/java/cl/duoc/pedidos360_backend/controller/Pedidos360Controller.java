package cl.duoc.pedidos360_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Version inicial en memoria: mismos campos (id, producto, estado) que ya
// consume pedidos.service.ts en el frontend. Cuando integres la base de datos
// cloud, esta clase debe delegar a un Service que use un Repository JPA
// en vez de devolver datos fijos.
//
// Datos de ejemplo pensados para Eco360: seguimiento de lotes de materiales
// reciclados, desde que se reciben hasta que quedan listos para despacho.
@RestController
@RequestMapping("/api/pedidos")
public class Pedidos360Controller {

    @GetMapping
    public List<Map<String, Object>> obtenerPedidos() {
        return List.of(
                Map.of("id", 1, "producto", "Botellas PET", "estado", "EN_CLASIFICACION"),
                Map.of("id", 2, "producto", "Carton prensado", "estado", "LISTO_PARA_DESPACHO"),
                Map.of("id", 3, "producto", "Vidrio molido", "estado", "EN_PROCESO")
        );
    }
}
